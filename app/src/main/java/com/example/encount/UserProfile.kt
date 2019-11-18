package com.example.encount

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class UserProfile : AppCompatActivity() {

    private val _helper = SQLiteHelper(this@UserProfile)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val actPost   = findViewById<View>(R.id.PostLine)
        val actFriend = findViewById<View>(R.id.FriendLine)
        val actStar   = findViewById<View>(R.id.LikeLine)

        val userPostBtn       = findViewById<LinearLayout>(R.id.UserPost)
        val userFriendBtn     = findViewById<LinearLayout>(R.id.UserFriend)
        val userLikeBtn       = findViewById<LinearLayout>(R.id.UserLike)
        val menuHomeBtn       = findViewById<LinearLayout>(R.id.MenuHome)
        val userSettingsBtn   = findViewById<ImageView>(R.id.UserSettings)

        actFriend.visibility = View.GONE
        actStar.visibility   = View.GONE

        UserDataGet().execute()
        //UserPostData().execute()

        userPostBtn.setOnClickListener {
            actPost.visibility   = View.VISIBLE
            actFriend.visibility = View.GONE
            actStar.visibility   = View.GONE

            UserPostData().execute()
        }

        userFriendBtn.setOnClickListener {
            actPost.visibility   = View.GONE
            actFriend.visibility = View.VISIBLE
            actStar.visibility   = View.GONE

            UserFriendData().execute()
        }

        userLikeBtn.setOnClickListener {
            actPost.visibility   = View.GONE
            actFriend.visibility = View.GONE
            actStar.visibility   = View.VISIBLE

            UserLikeData().execute()
        }

        menuHomeBtn.setOnClickListener {

            startActivity(Intent(this, UserHome::class.java))
            overridePendingTransition(0, 0)
        }

        userSettingsBtn.setOnClickListener {

            startActivity(Intent(this, UserSettings::class.java))
            overridePendingTransition(0, 0)
        }

    }

    private inner class UserDataGet() : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            var id     = ""
            val db     = _helper.writableDatabase
            val sql    = "select * from userInfo"
            val cursor = db.rawQuery(sql, null)

            while(cursor.moveToNext()){

                val idxId = cursor.getColumnIndex("user_id")
                id = cursor.getString(idxId)
            }

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://kinako.cf/UserDataGet.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("id", id)
            //リクエストの内容にformを追加
            val form = formBuilder.build()

            //リクエストを生成
            val request = Request.Builder().url(url).post(form).build()

            try {
                val response = client.newCall(request).execute()
                return response.body()!!.string()
            } catch (e: IOException) {
                e.printStackTrace()
                return "Error"
            }
        }

        override fun onPostExecute(result: String) {

            val txName = findViewById<TextView>(R.id.UserName)
            val txBio = findViewById<TextView>(R.id.UserBio)
            val userData = Gson().fromJson(result, UserDataClassList::class.java)
            txName.text = userData.userName
            txBio.text = userData.userBio
        }
    }

    private inner class UserPostData() : AsyncTask<String, String, String>() {

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

            val etName = findViewById<TextView>(R.id.UserName)
            val userData = Gson().fromJson(result, UserDataClassList::class.java)
            etName.text = userData.userName
        }
    }

    private inner class UserFriendData() : AsyncTask<String, String, String>() {

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

            val etName = findViewById<TextView>(R.id.UserName)
            val userData = Gson().fromJson(result, UserDataClassList::class.java)
            etName.text = userData.userName
        }
    }

    private inner class UserLikeData() : AsyncTask<String, String, String>() {

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

            val etName = findViewById<TextView>(R.id.UserName)
            val userData = Gson().fromJson(result, UserDataClassList::class.java)
            etName.text = userData.userName
        }
    }

}
