package com.example.encount

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.w3c.dom.Text
import java.io.IOException

class UserProfile : AppCompatActivity() {

    private val _helper = SQLiteHelper(this@UserProfile)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        UserDataPost().execute()

        prfCng.setOnClickListener {

            //3.Intentクラスのオブジェクトを生成。
            val intent = Intent(this, UserProfileChange::class.java)
            //生成したオブジェクトを引数に画面を起動！
            startActivity(intent)
        }

    }

    private inner class UserDataPost() : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            var id = ""
            val db = _helper.writableDatabase
            val sql = "select * from userInfo"
            val cursor = db.rawQuery(sql, null)

            while(cursor.moveToNext()){
                val idxId = cursor.getColumnIndex("user_id")

                id = cursor.getString(idxId)
                Log.d("DebugUser",id)
            }

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://kinako.cf/UserDataGet.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("id",id)
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
            val etName = findViewById<TextView>(R.id.userName)
            val etAtname = findViewById<TextView>(R.id.userAtName)
            val userData = Gson().fromJson(result, UserDataClassList::class.java)
            etName.text = userData.userName
            etAtname.text = userData.userAtname
        }
    }
}
