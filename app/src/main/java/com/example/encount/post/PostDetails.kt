package com.example.encount.post

import android.media.Image
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.encount.PostDataClassList
import com.example.encount.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_post_details.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception

/**
 * やってること
 * 投稿の詳細な情報を表示する
 *
 * 画像
 * ユーザーネーム
 * アイコン
 * いいねフラグ
 * テキスト
 * 投稿日時
 *
 * 製作者：中村
 */

class PostDetails : AppCompatActivity() {

    var postId = "a"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        postId = intent.getStringExtra("Post_Id")

        tvUserName.text = postId

        UserPostGet().execute()
    }

    private inner class UserPostGet() : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/PostDetailsGet.php"

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

                var postData = Gson().fromJson(result, PostDataClassList::class.java)

                Glide.with(this@PostDetails).load(postData.postImage).into(ivPostImage)
                tvUserName.text = postData.userName
                tvPostName.text = postData.userName
                tvPostDate.text = postData.postDate
                tvPostText.text = postData.postText
            }
            catch(e : Exception){

            }
        }
    }
}