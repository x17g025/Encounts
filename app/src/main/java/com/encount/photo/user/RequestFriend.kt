package com.encount.photo.user

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.encount.photo.*
import com.encount.photo.FriendList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_friend_request.*
import kotlinx.android.synthetic.main.fragment_user_home.swipelayout
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception

class RequestFriend : AppCompatActivity() {

    var _id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_request)

        swipelayout.setColorSchemeResources(R.color.colorMain)

        _id = doSelectSQLite(this)

        UserDataGet().execute()

        swipelayout.setOnRefreshListener {

            UserDataGet().execute()
        }
    }

    private inner class UserDataGet : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/RequestFriend.php"

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

            try {
                var requestList = mutableListOf<FriendList>()
                val listType = object : TypeToken<List<UserDataClassList>>() {}.type
                val friendData = Gson().fromJson<List<UserDataClassList>>(result, listType)

                for (i in friendData) {

                    requestList.add(
                        FriendList(
                            i.userId,
                            i.userName,
                            i.userIcon
                        )
                    )
                }

                lvRequest.adapter = RequestAdapter(this@RequestFriend, requestList)
                swipelayout.isRefreshing = false
            }
            catch(e : Exception){

                swipelayout.isRefreshing = false
                lvRequest.visibility = View.GONE
            }
        }
    }
}
