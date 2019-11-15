package com.example.encount

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_user_login.*
import okhttp3.*
import java.io.IOException

class UserLogin : AppCompatActivity() {

    private val _helper = SQLiteHelper(this@UserLogin)
    var mail = ""
    var pass = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)

        loginbtn.setOnClickListener {

            val etMail = findViewById<EditText>(R.id.usermail)
            val etPass = findViewById<EditText>(R.id.userpass)
            mail = etMail.text.toString()
            pass = etPass.text.toString()
            LoginDataPost().execute()
        }

        usernew.setOnClickListener {

            startActivity(Intent(this, UserSingin::class.java))
        }
    }

    private inner class LoginDataPost() : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://kinako.cf/LoginCheck.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
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

            Log.d("Debug",result)

            val db = _helper.writableDatabase
            var loginFlag = Gson().fromJson(result, LoginDataClassList::class.java)

            Log.d("Debug",loginFlag.result)
            if(loginFlag.userLoginFlag) {


                val sqlDelete = "delete from userInfo"
                var stmt = db.compileStatement(sqlDelete)
                stmt.executeUpdateDelete()

                val sqlInsert = "insert into userInfo (user_id) values (?)"
                stmt = db.compileStatement(sqlInsert)
                stmt.bindLong(1, loginFlag.userId)
                stmt.executeInsert()

                val sql = "select * from userInfo"
                val cursor = db.rawQuery(sql, null)

                while (cursor.moveToNext()) {
                    val test = cursor.getColumnIndex("user_id")
                    Log.d("Debug", cursor.getString(test))
                    goProflie()
                }
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
