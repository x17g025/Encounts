package com.example.encount

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.post_list.view.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PostAdapter(val context: Context, val posts: List<post>): BaseAdapter() {

    val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val _helper = SQLiteHelper(context)
    var postId = ""
    var likeFlag = true

    override fun getCount(): Int {
        return posts.count()
    }

    override fun getItem(position: Int): post {
        return posts[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = layoutInflater.inflate(R.layout.post_list, parent, false)
        view.PostUserName.text = posts[position].name
        view.PostUserText.text = posts[position].text
        view.PostDate.text     = posts[position].date
        Glide.with(context).load(posts[position].image).into(view.PostImage)
        postId = posts[position].postid

        return view
    }

    private inner class Like() : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            var userId = ""
            val db = _helper.writableDatabase
            val sql = "select * from userInfo"
            val cursor = db.rawQuery(sql, null)

            while(cursor.moveToNext()){

                val idxId = cursor.getColumnIndex("user_id")
                userId = cursor.getString(idxId)
            }

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://kinako.cf/encount/UserLikeSend.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("user",userId)
            formBuilder.add("post",postId)
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

            likeFlag = Gson().fromJson(result, like::class.java).likeFlag
        }
    }
}

