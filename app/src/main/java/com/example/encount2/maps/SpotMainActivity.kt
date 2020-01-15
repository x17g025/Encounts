package com.example.encount2.maps

/*
Copyright 2013 Square, Inc.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
   http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
//AndroidX

import android.os.AsyncTask
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.example.encount2.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.spotmain.*

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception

//import android.support.v7.app.AppCompatActivity;

//将来的には、マップ上でタップした位置情報を変数に代入して処理するようにする
var latitude = 35.704292
var longitude = 139.984092

class SpotMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.spotmain)

        SpotPhotoGet().execute()

        /*
        // GridViewのインスタンスを生成
        val gridview = findViewById<GridView>(R.id.gridview)

        // BaseAdapter を継承したGridAdapterのインスタンスを生成
        val adapter = GridAdapter(
            this.applicationContext,
            R.layout.grid_items,
            photos
        )

        // gridViewにadapterをセット
        gridview.setAdapter(adapter)

        //画面遷移用
        val menuHomeBtn = findViewById<LinearLayout>(R.id.MenuHome)
        val menuUserBtn = findViewById<LinearLayout>(R.id.MenuUser)
        val SpotPopular = findViewById<LinearLayout>(R.id.SpotPopular)
    }

    companion object {

        private val photos = arrayOf(
            "5ddb4b39131fd",
            "1896915185_5de706dbed01f",
            "1697161281_5ddc933768fec",
            "1858530768_5de645e06b5d7",
        )*/
    }

    private inner class SpotPhotoGet() : AsyncTask<String, String, String>(){

        override fun doInBackground(vararg params: String?): String {
            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/SpotInfoSend.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            println("経度"+latitude.toString())
            println("緯度"+longitude.toString())

            //Formに要素を追加
            formBuilder.add("latitude", latitude.toString())
            formBuilder.add("longitude", longitude.toString())

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
                var postList = mutableListOf<PostList2>()
                val listType = object : TypeToken<List<PostList2>>() {}.type
                val postData = Gson().fromJson<List<PostList2>>(result, listType)
                var postCount = 0

                for (i in postData) {

                    postCount++

                    postList.add(
                        PostList2(
                            i.imageId,
                            i.userId,
                            i.imagePath,
                            i.imageLat,
                            i.imageLng
                        )
                    )
                }

                SpotPopularCount.text = Integer.toString(postCount)
                SpotNewCount.text = Integer.toString(postCount)
                //print(Integer.toString(postCount))
                gridview.adapter = GridAdapter(this@SpotMainActivity, postList)
            }
            catch (e : Exception){

            }
        }





    }

}