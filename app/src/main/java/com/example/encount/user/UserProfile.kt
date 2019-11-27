package com.example.encount.user

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.encount.*
import com.example.encount.post.PostAdapter
import com.example.encount.post.UserHome
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_user_profile.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception

/**
 * やってること
 * ユーザのプロフィールを表示する
 *
 * 製作者：中村
 */

class UserProfile : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return  inflater.inflate(R.layout.activity_user_profile, container, false)

        /*FriendLine.visibility    = View.GONE
        LikeLine.visibility      = View.GONE
        FriendFindBtn.visibility = View.GONE

        UserDataGet().execute()
        UserPostGet().execute()

        UserPost.setOnClickListener {
            PostLine.visibility   = View.VISIBLE
            FriendLine.visibility = View.GONE
            FriendFindBtn.visibility = View.GONE
            LikeLine.visibility   = View.GONE

            UserPostGet().execute()
        }

        UserFriend.setOnClickListener {
            PostLine.visibility   = View.GONE
            FriendLine.visibility = View.VISIBLE
            FriendFindBtn.visibility = View.VISIBLE
            LikeLine.visibility   = View.GONE

            UserFriendGet().execute()
        }

        UserLike.setOnClickListener {
            PostLine.visibility   = View.GONE
            FriendLine.visibility = View.GONE
            FriendFindBtn.visibility = View.GONE
            LikeLine.visibility   = View.VISIBLE

            UserLikeGet().execute()
        }

        UserSettings.setOnClickListener {

            startActivity(Intent(this, com.example.encount.user.UserSettings::class.java))
        }

        FriendFindBtn.setOnClickListener {

            startActivity(Intent(this, com.example.encount.friend.FriendAdd::class.java))
        }
    }

    private inner class UserDataGet() : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val _helper = SQLiteHelper(this@UserProfile)

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

            try{
                val lvPost = findViewById<ListView>(R.id.UserDataList)
                var postList = mutableListOf<PostList>()
                val listType = object : TypeToken<List<PostDataClassList>>() {}.type
                val postData = Gson().fromJson<List<PostDataClassList>>(result, listType)
                var postCount = 0

                for (i in postData) {

                    postCount++

                    if(i.likeId == null){

                        postList.add(
                            PostList(
                                "null",
                                i.postId,
                                i.userId,
                                i.userName,
                                i.postText,
                                i.postDate,
                                i.postImage
                            )
                        )
                    }
                    else{

                        postList.add(
                            PostList(
                                i.likeId,
                                i.postId,
                                i.userId,
                                i.userName,
                                i.postText,
                                i.postDate,
                                i.postImage
                            )
                        )
                    }

                }
                UserPostCount.text = Integer.toString(postCount)
                lvPost.adapter = PostAdapter(this@UserProfile, postList)
            }
            catch (e : Exception){

            }
        }
    }

    //ユーザフレンドデータ取得
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
        }*/
    }
}
