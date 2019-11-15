package com.example.encount

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_user_singin.*
import okhttp3.*
import java.io.IOException

class UserSingin : AppCompatActivity() {

    private val _helper = SQLiteHelper(this@UserSingin)
    var name = ""
    var mail = ""
    var pass = ""
    var repass = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_singin)

        singinbtn.setOnClickListener {

            val etName = findViewById<EditText>(R.id.username)
            val etMail = findViewById<EditText>(R.id.usermail)
            val etPass = findViewById<EditText>(R.id.userpass)
            val etrePass = findViewById<EditText>(R.id.reuserpass)
            val etError = findViewById<TextView>(R.id.error)
            name = etName.text.toString()
            mail = etMail.text.toString()
            pass = etPass.text.toString()
            repass = etrePass.text.toString()

            if(pass == repass) {
                SinginDataPost().execute()
            }
            else{
                etError.text = "パスワードが一致しません"
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
            val url = "https://kinako.cf/UserSingin.php"

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

            val db = _helper.writableDatabase
            var singinFlag = Gson().fromJson(result, SinginDataClassList::class.java)

            if(singinFlag.userSinginFlag) {

                val sqlDelete = "delete from userInfo"
                var stmt = db.compileStatement(sqlDelete)
                stmt.executeUpdateDelete()

                val sqlInsert = "insert into userInfo (user_id) values (?)"
                stmt = db.compileStatement(sqlInsert)
                stmt.bindLong(1, singinFlag.userId)
                stmt.executeInsert()
                goProflie()
            }
            else{
                val etError = findViewById<TextView>(R.id.error)
                etError.text = singinFlag.result
            }
        }
    }

    override fun onDestroy() {
        _helper.close()
        super.onDestroy()
    }

    fun goProflie(){
        startActivity(Intent(this, UserProfile::class.java))
    }
}
