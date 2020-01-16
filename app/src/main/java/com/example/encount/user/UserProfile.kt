package com.example.encount.user

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.encount.*
import com.google.gson.Gson
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

class UserProfile : AppCompatActivity() {

    private val _helper = SQLiteHelper(this@UserProfile)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        UserDataList.adapter = TabAdapter(supportFragmentManager, this)
        //TabLayoutにViewPagerを設定
        tabLayout.setupWithViewPager(UserDataList)

        UserDataGet().execute()
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
            val url = "https://encount.cf/encount/UserDataGet.php"

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

            try{
                val userData = Gson().fromJson(result, UserDataClassList::class.java)
                UserName.text = userData.userName
                UserBio.text = userData.userBio
            }
            catch(e : Exception){
            }
        }
    }
}
