package com.encount.photo.post

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.encount.photo.*
import com.encount.photo.user.UserPost
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_user_home.*
import kotlinx.android.synthetic.main.ga_post_item.view.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception

/**
 * やってること
 * 投稿をサーバから取得してListViewに表示する
 *
 * 製作者：中村
 */

class UserHome : Fragment() {

    var postId = "a"
    var viewId : View? = null
    var _id = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_user_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        _id = doSelectSQLite(context)

        swipelayout.setColorSchemeResources(R.color.colorMain)

        UserPostGet().execute()

        //長押しでいいね

        gvPostData.setOnItemLongClickListener { parent, view, position, id ->

            viewId = view
            postId = view.tvPostId.text.toString()

            return@setOnItemLongClickListener true
        }

        btnPost.setOnClickListener{

            startActivity(Intent(context, UserPost::class.java))
        }

        swipelayout.setOnRefreshListener {

           UserPostGet().execute()
        }
    }

    private inner class UserPostGet : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/UserPostGet2.php"

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
                var postList = mutableListOf<PostList>()
                val listType = object : TypeToken<List<PostDataClassList>>() {}.type
                val postData = Gson().fromJson<List<PostDataClassList>>(result, listType)

                for (i in postData) {

                    postList.add(
                        PostList(
                            i.postId,
                            i.userId,
                            i.likeFlag,
                            i.postImage
                        )
                    )
                }

                gvPostData.adapter = PostAdapter(context, postList, "")
                swipelayout.isRefreshing = false
            }
            catch(e : Exception){

            }
        }
    }
}
