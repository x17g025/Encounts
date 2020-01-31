package com.encount.photo.user

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.encount.photo.*
import com.encount.photo.post.PostAdapter
import com.encount.photo.post.PostDetails
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.ga_post_item.view.*
import kotlinx.android.synthetic.main.tablayout_user_data.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception


class UserPostList : Fragment() {

    var _helper : SQLiteHelper? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.tablayout_user_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        _helper = SQLiteHelper(context)

        swipelayout.setColorSchemeResources(R.color.colorMain)

        UserPostGet().execute()

        //タップで投稿の詳細画面へ
        gvUserData.setOnItemClickListener {parent, view, position, id ->

            view.image_view.setOnClickListener {

                val intent = Intent(context, PostDetails::class.java)
                intent.putExtra("Post_Id", view.tvPostId.text)
                intent.putExtra("imageLat", view.tvImageLat.text)
                intent.putExtra("imageLng", view.tvImageLng.text)
                startActivity(intent)
            }
        }


        swipelayout.setOnRefreshListener {

            UserPostGet().execute()
        }
    }

    private inner class UserPostGet() : AsyncTask<String, String, String>() {

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
            val url = "https://encount.cf/encount/MyPostGet.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("id",id)
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
                var postList = mutableListOf<PostList>()
                val listType = object : TypeToken<List<PostDataClassList>>() {}.type
                val postData = Gson().fromJson<List<PostDataClassList>>(result, listType)

                for (i in postData) {

                    postList.add(
                        PostList(
                            i.postId,
                            i.userId,
                            i.likeFlag,
                            i.postImage,
                            i.imageLat,
                            i.imageLng
                        )
                    )
                }

                gvUserData.adapter = PostAdapter(context, postList)
                swipelayout.isRefreshing = false
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
