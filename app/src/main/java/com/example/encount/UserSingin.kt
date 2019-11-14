package com.example.encount

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_user_login.*
import okhttp3.*
import java.io.IOException

class UserSingin : AppCompatActivity() {

    var mail = ""
    var pass = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_singin)

        loginbtn.setOnClickListener {

            val etMail = findViewById<EditText>(R.id.usermail)
            val etPass = findViewById<EditText>(R.id.userpass)
            mail = etMail.text.toString()
            pass = etPass.text.toString()
            LoginDataPost().execute()

        }
    }

    private inner class LoginDataPost() : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://kinako.cf/UserSingin.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("mail",mail)
            formBuilder.add("name",mail)
            formBuilder.add("atname",mail)
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

            Log.d("Debug",result)
        }
    }
}
