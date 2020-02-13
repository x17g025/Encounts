package com.encount.photo.user

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.encount.photo.*
import com.encount.photo.post.TabAdapter
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

    var _id = ""
    var userId = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        _id = doSelectSQLite(this)

        try {

            if (intent.getStringExtra("User_Id")!!.isNotEmpty() && intent.getStringExtra("User_Id") != _id) {

                userId = intent.getStringExtra("User_Id")!!
                OtherDataGet().execute()
                UserDataList.adapter = TabAdapter(supportFragmentManager, this, userId)
                tabLayout.setupWithViewPager(UserDataList)
            }
            else {

                UserDataGet().execute()
                UserDataList.adapter = TabAdapter(supportFragmentManager, this, _id)
                tabLayout.setupWithViewPager(UserDataList)
            }
        }
        catch (e : Exception){

            UserDataGet().execute()
            UserDataList.adapter = TabAdapter(supportFragmentManager, this, _id)
            tabLayout.setupWithViewPager(UserDataList)
        }

        //プロフィール変更
        ivChange.setOnClickListener {

            startActivity(Intent(this, ProfileChange::class.java))
        }

        ivFollow.setOnClickListener {

            FriendToggle().execute()
        }
    }

    private inner class UserDataGet : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/UserDataGet.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("id", _id)
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

                ivChange.visibility = View.VISIBLE
                Glide.with(this@UserProfile).load(userData.userIcon).into(ivUserIcon)
                UserName.text = userData.userName
                UserBio.text = userData.userBio
            }
            catch(e : Exception){
            }
        }
    }

    private inner class OtherDataGet : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/OtherDataGet.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("id", _id)
            formBuilder.add("other_id", userId)
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

                Glide.with(this@UserProfile).load(userData.userIcon).into(ivUserIcon)
                UserName.text = userData.userName
                UserBio.text = userData.userBio

                when (userData.followFlag) {

                    2 -> {
                        ivFollow.setImageResource(R.drawable.tool_wait)
                    }
                    1 -> {
                        ivFollow.setImageResource(R.drawable.tool_check)
                    }
                    else -> {
                        ivFollow.setImageResource(R.drawable.tool_add)
                    }
                }
                ivFollow.visibility = View.VISIBLE

            }
            catch(e : Exception){
            }
        }
    }

    private inner class FriendToggle : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/FriendToggle.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("id", _id)
            formBuilder.add("other", userId)
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

                when (userData.followFlag) {

                    2 -> {
                        ivFollow.setImageResource(R.drawable.tool_wait)
                    }
                    1 -> {
                        ivFollow.setImageResource(R.drawable.tool_check)
                    }
                    else -> {
                        ivFollow.setImageResource(R.drawable.tool_add)
                    }
                }
            }
            catch(e : Exception){
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        if (keyCode == KEYCODE_BACK) {

            startActivity(Intent(this, NavigationActivity::class.java))
            return true
        }
        return false
    }
}
