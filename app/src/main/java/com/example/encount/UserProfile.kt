package com.example.encount

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_user_profile.*
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

        val userPostBtn     = findViewById<LinearLayout>(R.id.UserPost)
        val userFriendBtn   = findViewById<LinearLayout>(R.id.UserFriend)
        val userLikeBtn     = findViewById<LinearLayout>(R.id.UserLike)
        val menuHomeBtn     = findViewById<LinearLayout>(R.id.MenuHome)
        val userSettingsBtn = findViewById<ImageView>(R.id.UserSettings)

        actFriend.visibility = View.GONE
        actStar.visibility   = View.GONE

        UserDataGet().execute()
        UserPostGet().execute()

        userPostBtn.setOnClickListener {
            actPost.visibility   = View.VISIBLE
            actFriend.visibility = View.GONE
            actStar.visibility   = View.GONE

            UserPostGet().execute()
        }

        userFriendBtn.setOnClickListener {
            actPost.visibility   = View.GONE
            actFriend.visibility = View.VISIBLE
            actStar.visibility   = View.GONE

            UserFriendGet().execute()
        }

        userLikeBtn.setOnClickListener {
            actPost.visibility   = View.GONE
            actFriend.visibility = View.GONE
            actStar.visibility   = View.VISIBLE

            UserLikeGet().execute()
        }

        menuHomeBtn.setOnClickListener {

            startActivity(Intent(this, UserHome::class.java))
            overridePendingTransition(0, 0)
        }

        userSettingsBtn.setOnClickListener {

            startActivity(Intent(this, com.example.encount.UserSettings::class.java))
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
            val url = "https://kinako.cf/encount/UserDataGet.php"

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

    //ユーザ投稿データ取得
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
            val url = "https://kinako.cf/encount/UserPostGet.php"

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
                val lvPost = findViewById<ListView>(R.id.UserPostList)
                var postList = mutableListOf<post>()
                val listType = object : TypeToken<List<PostDataClassList>>() {}.type
                val postData = Gson().fromJson<List<PostDataClassList>>(result, listType)
                var postCount = 0

                for (i in postData) {

                    postCount++

                    if(i.likeId == null){

                        postList.add(post("null", i.postId, i.userId, i.userName, i.postText, i.postDate, i.postImage))
                    }
                    else{

                        postList.add(post(i.likeId, i.postId, i.userId, i.userName, i.postText, i.postDate, i.postImage))
                    }

                }
                UserPostCount.text = Integer.toString(postCount)
                lvPost.adapter = PostAdapter(this@UserProfile, postList)
            }
        }
    }

    private inner class UserFriendGet() : AsyncTask<String, String, String>() {

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
            val url = "https://kinako.cf/encount/UserDataGet.php"

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

    private inner class UserLikeGet() : AsyncTask<String, String, String>() {

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
            val url = "https://kinako.cf/encount/UserDataGet.php"

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
