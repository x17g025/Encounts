package com.encount.photo.user

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import cn.pedant.SweetAlert.SweetAlertDialog
import com.encount.photo.R
import com.encount.photo.SQLiteHelper
import com.encount.photo.SinginDataClassList
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_app_singin.*
import okhttp3.*
import java.io.IOException

/**
 * やってること
 * 新規登録項目をサーバに送信し、サーバから新規登録成功(true)が帰ってきたらホーム画面に飛ばす
 *
 * 製作者：中村
 */

class UserSingin : AppCompatActivity() {

    private val _helper = SQLiteHelper(this@UserSingin)
    var name = ""
    var mail = ""
    var pass = ""
    var repass = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_singin)

        singinProgress.visibility = View.GONE

        singinbtn.setOnClickListener {

            name = username.text.toString()
            mail = usermail.text.toString()
            pass = userpass.text.toString()
            repass = reuserpass.text.toString()
            info.text = ""

            if(pass == repass) {

                if(name != "" && pass != "" && mail != ""){

                    singinProgress.visibility = View.VISIBLE
                    SinginDataPost().execute()
                }
                else{

                    info.text = "入力されていない項目があります"
                }
            }
            else{

                info.text = "パスワードが一致しません"
            }
        }

        userlogin.setOnClickListener {

            startActivity(Intent(this, UserLogin::class.java))
        }
    }

    private inner class SinginDataPost() : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/UserSingin.php"

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

                SweetAlertDialog(this@UserSingin, SweetAlertDialog.SUCCESS_TYPE)
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

    override fun onDestroy() {
        _helper.close()
        super.onDestroy()
    }

    fun goLogin(){

        startActivity(Intent(this, UserLogin::class.java))
    }
}