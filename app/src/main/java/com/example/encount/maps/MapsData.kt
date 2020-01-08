package com.example.encount.maps


import android.os.AsyncTask
import android.util.Log
import com.example.encount.MainActivity
import com.example.encount.MapsDataClassList
import com.example.encount.MapsList

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException as IoIOException

class MapPostGet(val activity: MapsHome) : AsyncTask<String, String, String>() {

    override fun doInBackground(vararg params: String): String {
        Log.d("debug", "background")
        val client = OkHttpClient()

        //アクセスするURL
        val url = "https://encount.cf/encount/MapsDataGet.php"

        //Formを作成
        val formBuilder = FormBody.Builder()

        //formに要素を追加

        //リクエストの内容にformを追加
        val form = formBuilder.build()

        //リクエストを生成
        val request = Request.Builder().url(url).post(form).build()



        try {
            //受信用
            val response = client.newCall(request).execute()
            return response.body()!!.string()
        } catch (e: IoIOException) {
            e.printStackTrace()
            return "Error"
        }
    }


    //結果表示
    override fun onPostExecute(result: String) {

        try {

            var postList = mutableListOf<MapsList>()
            val listType = object : TypeToken<List<MapsDataClassList>>() {}.type
            val postData = Gson().fromJson<List<MapsDataClassList>>(result, listType)

            for (i in postData) {
                postList.add(
                    MapsList(
                        i.imgPath,
                        i.imgLat,
                        i.imgLng
                    )
                )
            }
            Log.d("debug", "background result")
            //Log.d("debug", "pass" + postList[1].imgpath)
            activity.setPostList(postList)
        } catch (e: Exception) {

        }
    }
}