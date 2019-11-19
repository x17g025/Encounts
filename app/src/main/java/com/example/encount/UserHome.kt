package com.example.encount

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class UserHome : AppCompatActivity() {

    private val _helper = SQLiteHelper(this@UserHome)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)

        val menuUserBtn = findViewById<LinearLayout>(R.id.MenuUser)

        UserPostGet().execute()
        menuUserBtn.setOnClickListener {

            startActivity(Intent(this, UserProfile::class.java))
            overridePendingTransition(0, 0)
        }
    }

    private inner class UserPostGet() : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            var id = ""
            val db = _helper.writableDatabase
            val sql = "select * from userInfo"
            val cursor = db.rawQuery(sql, null)

            while(cursor.moveToNext()){

                val idxId = cursor.getColumnIndex("user_id")
                id = cursor.getString(idxId)
            }

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://kinako.cf/UserPostGet.php"

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

            if(result != null || result != "") {
                val lvPost = findViewById<ListView>(R.id.PostDataList)
                var postList = mutableListOf<post>()
                val listType = object : TypeToken<List<PostDataClassList>>() {}.type
                val postData = Gson().fromJson<List<PostDataClassList>>(result, listType)

                for (i in postData) {

                    postList.add(post(i.userName, i.postText))
                    Log.d("a",i.postDate)
                }

                lvPost.adapter = PostAdapter(this@UserHome, postList)
            }

        }
    }
}