package com.example.encount.post

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.example.encount.*
import com.example.encount.post.UserPost
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_friend_profile.*
import kotlinx.android.synthetic.main.activity_post_details.*
import kotlinx.android.synthetic.main.activity_user_home.*
import kotlinx.android.synthetic.main.grid_items.view.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception

/**
 * やってること
 * 投稿をサーバから取得してListViewに表示する
 *
 * 製作者：中村
 */

class UserHome : Fragment() {

    var _helper : SQLiteHelper? = null
    var postId = "a"
    var viewId : View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.activity_user_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        _helper = SQLiteHelper(context)

        swipelayout.setColorSchemeResources(R.color.colorMain)

        UserPostGet().execute()

        //タップで投稿の詳細画面へ
        PostDataList.setOnItemClickListener {parent, view, position, id ->

            view.image_view.setOnClickListener {

                val intent = Intent(context, PostDetails::class.java)
                intent.putExtra("Post_Id", view.tvPostId.text)
                startActivity(intent)
            }

            view.ivPostLike.setOnClickListener{

                viewId = view
                postId = view.tvPostId.text.toString()
                Log.d("baba", postId)
                UserPostLike().execute()
            }
        }

        //長押しでいいね
        PostDataList.setOnItemLongClickListener { parent, view, position, id ->

            viewId = view
            postId = view.tvPostId.text.toString()
            Log.d("baba", postId)
            UserPostLike().execute()

            return@setOnItemLongClickListener true
        }

        btnPost.setOnClickListener{

            val intent = Intent(context, UserPost::class.java)
            startActivity(intent)
        }

        swipelayout.setOnRefreshListener {

           UserPostGet().execute()
        }
    }

    private inner class UserPostGet : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            var id = ""
            val db = _helper!!.writableDatabase
            val sql = "select * from userInfo"
            val cursor = db.rawQuery(sql, null)

            while (cursor.moveToNext()) {

                val idxId = cursor.getColumnIndex("user_id")
                id = cursor.getString(idxId)
            }

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/UserPostGet.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("id", id)
            //リクエストの内容にformを追加
            val form = formBuilder.build()

            //リクエストを生成
            val request = Request.Builder().url(url).post(form).build()

            try {
                val response = client.newCall(request).execute()
                return response.body()!!.string()
            } catch (e: IOException) {
                e.printStackTrace()
                return "Error"
            }
        }

        override fun onPostExecute(result: String) {

            try {
                var postList = mutableListOf<PostList>()
                val listType = object : TypeToken<List<PostDataClassList>>() {}.type
                val postData = Gson().fromJson<List<PostDataClassList>>(result, listType)

                for (i in postData) {

                    postList.add(
                        PostList(
                            i.postId,
                            i.userId,
                            i.likeFlag,
                            i.postImage
                        )
                    )
                }

                PostDataList.adapter = PostAdapter(context, postList)
                swipelayout.isRefreshing = false
            }
            catch(e : Exception){

            }
        }
    }

    private inner class UserPostLike : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            var id = ""
            val db = _helper!!.writableDatabase
            val sql = "select * from userInfo"
            val cursor = db.rawQuery(sql, null)

            while(cursor.moveToNext()){

                val idxId = cursor.getColumnIndex("user_id")
                id = cursor.getString(idxId)
            }

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/UserLikeSend.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("user",id)
            formBuilder.add("post",postId)
            //リクエストの内容にformを追加
            val form = formBuilder.build()

            //リクエストを生成
            val request = Request.Builder().url(url).post(form).build()

            try {
                val response = client.newCall(request).execute()
                return response.body()!!.string()
            }
            catch (e: IOException) {
                e.printStackTrace()
                return "Error"
            }
        }

        override fun onPostExecute(result: String) {

            try {

                var likeFlag = Gson().fromJson(result, like::class.java)

                if(likeFlag.flag) {

                    viewId!!.ivPostLike.setImageResource(R.drawable.post_tool_like_true)
                    var animation = AnimationUtils.loadAnimation(context,R.anim.like_touch)
                    viewId!!.ivPostLike.startAnimation(animation)
                }
                else{

                    viewId!!.ivPostLike.setImageResource(R.drawable.post_tool_like_false)
                    var animation = AnimationUtils.loadAnimation(context,R.anim.like_touch)
                    viewId!!.ivPostLike.startAnimation(animation)
                }

            }
            catch(e : Exception){

            }
        }
    }

    override fun onDestroy(){

        //ヘルパーオブジェクトの開放
        _helper!!.close()
        super.onDestroy()
    }
}
