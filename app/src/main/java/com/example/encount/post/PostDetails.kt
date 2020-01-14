package com.example.encount.post

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.encount.PostDataClassList
import com.example.encount.PostList
import com.example.encount.R
import com.example.encount.SQLiteHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
 * 投稿の詳細な情報を表示する
 *
 * 製作者：中村
 */

class PostDetails : AppCompatActivity() {

    val postId = intent.getStringExtra("Post_Id")

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        tvUserName.text = postId
    }

    private inner class UserPostGet() : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/UserPostGet.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("id",postId)
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

                Glide.with(this@PostDetails).load(postData.).into(ivPostImage)

                for (i in postData) {

                    postList.add(
                        PostList(
                            i.postId,
                            i.userId,
                            i.postText,
                            i.postImage
                        )
                    )
                }
            }
            catch(e : Exception){

            }
        }
    }
}