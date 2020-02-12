package com.encount.photo.post

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.encount.photo.R
import com.encount.photo.SQLiteHelper
import com.encount.photo.doSelectSQLite
import kotlinx.android.synthetic.main.activity_user_do_post.*
import okhttp3.*
import java.io.IOException

/**
 * 投稿機能(カメラ、コメント、位置情報)
 * 制作者：大野、社務
 */

class PostReply : AppCompatActivity() {

    var _id = ""
    var postId = ""
    var userId = ""
    var text   = ""
    var preAct = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_do_post)

        _id = doSelectSQLite(this)

        postId = intent.getStringExtra("Post_Id")!!
        userId = intent.getStringExtra("User_Id")!!
        preAct = intent.getStringExtra("Pre_Act")!!

        postButton.setOnClickListener {

            text = commentInput.text.toString()

            if(text != ""){

                UserReplySend().execute()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KEYCODE_BACK) {

            transAct()
            return true
        }
        return false
    }


    private inner class UserReplySend : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/PostReplySend.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("user",_id)
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

        val intent = Intent(this, PostReplyList::class.java)
        intent.putExtra("Post_Id", postId)
        intent.putExtra("User_Id", userId)
        intent.putExtra("Pre_Act", preAct)
        startActivity(intent)
    }
}