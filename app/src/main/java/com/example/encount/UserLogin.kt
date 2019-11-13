package com.example.encount

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_user_login.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class UserLogin : AppCompatActivity() {

    private val _helper = SQLiteHelper(this@UserLogin)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)

        loginbtn.setOnClickListener {

            val etMail = findViewById<EditText>(R.id.usermail)
            val mailAdress = etMail.text.toString()
            val db = _helper.writableDatabase

            val sqlDel = "delete from userInfo"
            var stmt = db.compileStatement(sqlDel)
            stmt.executeUpdateDelete()

            val sqlIns = "insert into userInfo (user_email) values ( ? )"
            stmt = db.compileStatement(sqlIns)
            stmt.bindString(1, mailAdress)
            stmt.executeInsert()


        }
    }

    override fun onDestroy() {

        //ヘルパーオブジェクトの開放
        _helper.close()
        super.onDestroy()
    }

    private inner class LoginDataPost() : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val db = _helper.writableDatabase
            val sql = "select * from userInfo where _id = 1"
            val cursor = db.rawQuery(sql, null)

            var mail = ""

            while(cursor.moveToNext()){

                val idxMail = cursor.getColumnIndex("user_mail")

                mail = cursor.getString(idxMail)
            }

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://kinako.cf/LoginCheck.php"

            //Map<String, String> formParamMap = new HashMap<>();
            //formParamMap.put("word", "abc");

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formParamMap.forEach(formBuilder::add);

            //formに要素を追加
            formBuilder.add("mail","maaaaa")
            //リクエストの内容にformを追加
            val body = formBuilder.build()

            //RequestBody body = RequestBody.create(JSON, json);

            //リクエストを生成
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            try {
                val response = client.newCall(request).execute()
                return response.body()!!.string()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return "A"
        }

        override fun onPostExecute(result: String) {

            Log.d("Debug",result)
        }
    }
}
