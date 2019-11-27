package com.example.encount.post

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.encount.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_user_home.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import com.example.encount.friend.FriendProfile
import com.example.encount.user.UserProfile
import java.lang.Exception

/**
 * やってること
 * 投稿をサーバから取得してListViewに表示する
 *
 * 製作者：中村
 */

class UserHome : Fragment() {

    //private val _helper = SQLiteHelper(this@UserHome)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return  inflater.inflate(R.layout.activity_user_home, container, false)

        /*val menuUserBtn = findViewById<LinearLayout>(R.id.MenuUser)

        UserPostGet().execute()

        PostDataList.setOnItemClickListener {parent, view, position, id ->

            val postId = view.findViewById<TextView>(R.id.PostId).text

            val intent = Intent(this, PostDetails::class.java)
            intent.putExtra("Post_Id", postId)
            startActivity(intent)
        }

        PostDataList.setOnItemLongClickListener { parent, view, position, id ->

            val userId = view.findViewById<TextView>(R.id.UserId).text

            var id     = ""
            val db     = _helper.writableDatabase
            val sql    = "select * from userInfo"
            val cursor = db.rawQuery(sql, null)

            while(cursor.moveToNext()){

                val idxId = cursor.getColumnIndex("user_id")
                id = cursor.getString(idxId)
            }

            if(userId == id){

                startActivity(Intent(this, UserProfile::class.java))
            }
            else{

                val intent = Intent(this, FriendProfile::class.java)
                intent.putExtra("User_Id", userId)
                startActivity(intent)
            }

            return@setOnItemLongClickListener true
        }

        menuUserBtn.setOnClickListener {

            startActivity(Intent(this, UserProfile::class.java))
            overridePendingTransition(0, 0)
        }

        btnPost.setOnClickListener{

            startActivity(Intent(this, UserPost::class.java))
        }

        swipelayout.setOnRefreshListener {
            UserPostGet().execute()
        }
    }

    private inner class UserPostGet() : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            var id = ""
            val db = _helper.writableDatabase
            val sql = "select * from userInfo"
            val cursor = db.rawQuery(sql, null)

            while(cursor.moveToNext()){

                val idxId = cursor.getColumnIndex("user_id")
                id = cursor.getString(idxId)
            }

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://kinako.cf/encount/UserPostGet.php"

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
                val lvPost = findViewById<ListView>(R.id.PostDataList)
                var postList = mutableListOf<PostList>()
                val listType = object : TypeToken<List<PostDataClassList>>() {}.type
                val postData = Gson().fromJson<List<PostDataClassList>>(result, listType)

                for (i in postData) {

                    if(i.likeId == null){

                        postList.add(
                            PostList(
                                "false",
                                i.postId,
                                i.userId,
                                i.userName,
                                i.postText,
                                i.postDate,
                                i.postImage
                            )
                        )
                    }
                    else{

                        postList.add(
                            PostList(
                                i.likeId,
                                i.postId,
                                i.userId,
                                i.userName,
                                i.postText,
                                i.postDate,
                                i.postImage
                            )
                        )
                    }
                }
                postList[1].postid

                lvPost.adapter = PostAdapter(this@UserHome, postList)
            }
            catch(e : Exception){

            }

            swipelayout.isRefreshing = false
        }*/
    }
}
