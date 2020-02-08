package com.encount.photo.post

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.encount.photo.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.tablayout_post_data.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception

class UserPostList(Id : String) : Fragment() {

    var inId = ""
    var userId = Id

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.tablayout_post_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        inId = doSelectSQLite(context)

        swipelayout.setColorSchemeResources(R.color.colorMain)

        UserPostGet().execute()

        swipelayout.setOnRefreshListener {

            UserPostGet().execute()
        }
    }

    private inner class UserPostGet() : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/MyPostGet.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("id",userId)
            formBuilder.add("likeId",inId)
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
                var postList = mutableListOf<PostList>()
                val listType = object : TypeToken<List<PostDataClassList>>() {}.type
                val postData = Gson().fromJson<List<PostDataClassList>>(result, listType)

                for (i in postData) {

                    postList.add(
                        PostList(
                            i.postId,
                            i.userId,
                            i.likeFlag,
                            i.postImage,
                            "my"
                        )
                    )
                }

                gvPostData.adapter = PostAdapter(context, postList, userId)
                swipelayout.isRefreshing = false
            }
            catch(e : Exception){

            }
        }
    }
}
