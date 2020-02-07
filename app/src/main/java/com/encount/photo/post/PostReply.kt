package com.encount.photo.post

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.encount.photo.R
import com.encount.photo.SQLiteHelper
import kotlinx.android.synthetic.main.activity_user_do_post.*
import okhttp3.*
import java.io.IOException

/**
 * 投稿機能(カメラ、コメント、位置情報)
 * 制作者：大野、社務
 */

class PostReply : AppCompatActivity() {

    val _helper = SQLiteHelper(this@PostReply)
    var postId = ""
    var userId = ""
    var text   = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_do_post)

        postId = intent.getStringExtra("Post_Id")

        postButton.setOnClickListener {

            text = commentInput.text.toString()

            if(text != ""){

                UserReplySend().execute()
            }
        }
    }

    private inner class UserReplySend() : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            var userId = ""
            val db = _helper.writableDatabase
            val sql = "select * from userInfo"
            val cursor = db.rawQuery(sql, null)

            while(cursor.moveToNext()){

                val idxId = cursor.getColumnIndex("user_id")
                userId = cursor.getString(idxId)
            }

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/PostReplySend.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("user",userId)
            formBuilder.add("post",postId)
            formBuilder.add("text",text)
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

            SweetAlertDialog(this@PostReply, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("投稿完了")
                .setContentText("")
                .setConfirmText("OK")
                .setConfirmClickListener {
                        sDialog -> sDialog.dismissWithAnimation()
                        transAct()
                }
                .show()
        }
    }

    fun transAct(){

        val intent = Intent(this, PostDetails::class.java)
        intent.putExtra("Post_Id", postId)
        startActivity(intent)
    }
}