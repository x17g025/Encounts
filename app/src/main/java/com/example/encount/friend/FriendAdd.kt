package com.example.encount.friend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.os.AsyncTask
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.example.encount.*
import com.example.encount.post.PostAdapter
import com.example.encount.post.PostDetails
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_friend_add.*
import kotlinx.android.synthetic.main.activity_user_home.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception


class FriendAdd : AppCompatActivity() {

    private val _helper = SQLiteHelper(this@FriendAdd)
    var friendId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_add)

        val userId = findViewById<EditText>(R.id.userid)

        userId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //文字入力の前に行う処理を書く場所
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //文字入力の最中に行う処理を書く場所
            }
            override fun afterTextChanged(s: Editable?) {
                //文字入力の後に行う処理を書く場所

                if(userId.length() == 8){

                    FriendFind()
                }

            }
        })

        FriendDataList.setOnItemClickListener {parent, view, position, id ->

            friendId = view.findViewById<TextView>(R.id.UserId).text

            FriendAd()
        }
    }

    private inner class FriendFind() : AsyncTask<String, String, String>() {

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

            try {
                val lvPost = findViewById<ListView>(R.id.PostDataList)
                var friendList = mutableListOf<PostList>()
                val listType = object : TypeToken<List<PostDataClassList>>() {}.type
                val postData = Gson().fromJson<List<PostDataClassList>>(result, listType)




            }
            catch(e : Exception){

            }

            swipelayout.isRefreshing = false
        }
    }

    private inner class FriendAd() : AsyncTask<String, String, String>() {

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

            try {
                val lvPost = findViewById<ListView>(R.id.PostDataList)
                var friendList = mutableListOf<PostList>()
                val listType = object : TypeToken<List<PostDataClassList>>() {}.type
                val postData = Gson().fromJson<List<PostDataClassList>>(result, listType)




            }
            catch(e : Exception){

            }

            swipelayout.isRefreshing = false
        }
    }
}
