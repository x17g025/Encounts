package com.example.encount.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.encount.*

/**
 * やってること
 *
 *
 * 製作者：中村
 */

class FriendAdd : Fragment() {

   // private val _helper = SQLiteHelper(this@FriendAdd)
    //var friendId : String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return  inflater.inflate(R.layout.activity_friend_add, container, false)
        /*

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

           // friendId = view.findViewById<TextView>(R.id.UserId).text

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
            val url = "https://encount.cf/encount/UserPostGet.php"

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
            val url = "https://encount.cf/encount/UserPostGet.php"

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
        }*/
    }
}
