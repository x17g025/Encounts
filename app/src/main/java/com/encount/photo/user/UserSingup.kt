package com.encount.photo.user

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import cn.pedant.SweetAlert.SweetAlertDialog
import com.encount.photo.R
import com.encount.photo.SQLiteHelper
import com.encount.photo.SinginDataClassList
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_app_singup.*
import okhttp3.*
import java.io.IOException
import java.util.regex.Pattern

/**
 * やってること
 * 新規登録項目をサーバに送信し、サーバから新規登録成功(true)が帰ってきたらホーム画面に飛ばす
 *
 * 製作者：中村
 */

class UserSingup : AppCompatActivity() {

    var name = ""
    var mail = ""
    var pass = ""
    var repass = ""
    var checkFlag  = true

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_singup)

        singinProgress.visibility = View.GONE

        singinbtn.setOnClickListener {

            name = username.text.toString()
            mail = usermail.text.toString()
            pass = userpass.text.toString()
            repass = reuserpass.text.toString()
            info.text = ""

            checkFlag  = true

            checkPass()
            checkMail()

            if(checkFlag) {

                if (name.isNotEmpty()) {

                    name = "名無しさん"
                }

                singinProgress.visibility = View.VISIBLE
                SinginDataPost().execute()
            }
        }

        userlogin.setOnClickListener {

            startActivity(Intent(this, UserSingin::class.java))
        }
    }

    private fun checkMail(){

        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {

            usermail.error = "メールアドレスが不適切です"
            checkFlag = false
        }
    }

    private fun checkPass(){

        val p = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{6,}$")

        if(!p.matcher(pass).matches() || pass.isEmpty()) {

            userpass.error = "パスワードは以下を使用ください\n" +
                             "・小文字の半角アルファベット\n" +
                             "・大文字の半角アルファベット\n" +
                             "・半角数字\n" +
                             "・6文字以上"
            checkFlag = false
        }

        if(pass != repass || repass.isEmpty()){

            reuserpass.error = "パスワードが一致しません"
            checkFlag = false
        }
    }

    private inner class SinginDataPost() : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/UserSingup.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("name",name)
            formBuilder.add("mail",mail)
            formBuilder.add("pass",pass)
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

            val singinFlag = Gson().fromJson(result, SinginDataClassList::class.java)
            singinProgress.visibility = View.GONE

            if(singinFlag.flag) {

                SweetAlertDialog(this@UserSingup, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("新規登録")
                    .setContentText("登録されたメールアドレスに確認メールを送りました")
                    .setConfirmText("ログイン画面へ")
                    .setConfirmClickListener {
                        sDialog -> sDialog.dismissWithAnimation()
                        goLogin()
                    }
                    .show()
            }
            else{

                info.text = singinFlag.result
            }
        }
    }

    fun goLogin(){

        startActivity(Intent(this, UserSingin::class.java))
    }
}