package com.encount.photo.maps

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.encount.photo.*
import com.encount.photo.post.PostAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.tablayout_post_data.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception

class SpotNewPost : Fragment() {

    var postId = ""
    var inId = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.tablayout_post_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        inId = doSelectSQLite(context)

        swipelayout.setColorSchemeResources(R.color.colorMain)

        NewPhotoGet().execute()

        swipelayout.setOnRefreshListener {

            NewPhotoGet().execute()
        }
    }

    private inner class NewPhotoGet() : AsyncTask<String, String, String>(){

        override fun doInBackground(vararg params: String?): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/SpotInfoNew.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            println("経度"+latitude.toString())
            println("緯度"+longitude.toString())
            println("ユーザ"+inId)

            //Formに要素を追加
            formBuilder.add("latitude", latitude.toString())
            formBuilder.add("longitude", longitude.toString())
            formBuilder.add("user",inId)

            //リクエスト内容にformを追加
            val form = formBuilder.build()

            //リクエストを生成
            val request = Request.Builder().url(url).post(form).build()

            try {
                val response = client.newCall(request).execute()
                println(url)
                return response.body()!!.string()
            }catch (e: IOException){
                e.printStackTrace()
                return "Error"
            }
        }


        override fun onPostExecute(result: String) {
            try{
                var postList = mutableListOf<PostList>()
                val listType = object : TypeToken<List<PostDataClassList>>() {}.type
                val postData = Gson().fromJson<List<PostDataClassList>>(result, listType)
                var postCount = 0

                for (i in postData) {

                    postCount++

                    postList.add(
                        PostList(
                            i.postId,
                            i.userId,
                            i.likeFlag,
                            i.postImage,
                            "spot"
                        )
                    )
                }

                //print(Integer.toString(postCount))
                gvPostData.adapter = PostAdapter(context, postList, "")
                swipelayout.isRefreshing = false
            }
            catch (e : Exception){

            }
        }
    }
}