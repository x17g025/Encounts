package com.encount.photo.post

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import com.encount.photo.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_reply_list.*
import kotlinx.android.synthetic.main.fragment_user_home.swipelayout
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception

class PostReplyList : AppCompatActivity() {

    var _id = ""
    var postId = ""

    var text   = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply_list)

        swipelayout.setColorSchemeResources(R.color.colorMain)

        postId = intent.getStringExtra("Post_Id")!!

        _id = doSelectSQLite(this)

        UserReplyGet().execute()

        swipelayout.setOnRefreshListener {

            UserReplyGet().execute()
        }

        btnReply.setOnClickListener {

            startActivity(
                Intent(this, PostReply::class.java)
                    .putExtra("Post_Id", postId))
        }
    }

    private inner class UserReplyGet : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/PostReplyGet.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("post", postId)
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

                if(result.isNotEmpty()) {
                    var postList = mutableListOf<ReplyList>()
                    val listType = object : TypeToken<List<PostDataClassList>>() {}.type
                    val postData = Gson().fromJson<List<PostDataClassList>>(result, listType)

                    for (i in postData) {

                        postList.add(
                            ReplyList(
                                i.userId,
                                i.userName,
                                i.postText,
                                i.postDate
                            )
                        )
                    }

                    lvReply.adapter = ReplyAdapter(this@PostReplyList, postList)
                    swipelayout.isRefreshing = false
                }else{

                    swipelayout.isRefreshing = false
                }

            } catch (e: Exception) {

                swipelayout.isRefreshing = false
            }
        }
    }
}
