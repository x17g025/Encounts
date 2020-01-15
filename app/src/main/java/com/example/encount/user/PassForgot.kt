package com.example.encount.user

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.encount.LoginDataClassList
import com.example.encount.R
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_pass_forget.*
import kotlinx.android.synthetic.main.activity_pass_forget.Progress
import kotlinx.android.synthetic.main.activity_pass_forget.etUserMail
import kotlinx.android.synthetic.main.activity_pass_forget.txInfo
import kotlinx.android.synthetic.main.activity_user_login.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 * やってること
 * 謎
 *
 * 製作者：中村
 */

class PassForgot : AppCompatActivity() {

    var mail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_forget)

        Progress.visibility = View.GONE

        btnSearch.setOnClickListener {

            Progress.visibility = View.VISIBLE
            mail = etUserMail.text.toString()

            SearchAccount().execute()
        }
    }

    private inner class SearchAccount : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/UserAuth.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("mail",mail)
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

            var searchFlag = Gson().fromJson(result, LoginDataClassList::class.java)
            Progress.visibility = View.GONE

            if(searchFlag.flag) {

                SweetAlertDialog(this@PassForgot, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("パスワード再発行")
                    .setContentText("登録されたメールアドレスに再発行メールを送りました")
                    .setConfirmText("ログイン画面へ")
                    .setConfirmClickListener {
                            sDialog -> sDialog.dismissWithAnimation()
                    }
                    .show()
            }
            else{

                txInfo.text = searchFlag.result
            }
        }
    }
}
