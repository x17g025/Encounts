package com.example.encount.maps

import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import com.example.encount.*
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_user_home.*
import kotlinx.android.synthetic.main.spotmain.*

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception

var latitude = 35.704292
var longitude = 139.984092

class SpotMainActivity : AppCompatActivity() {

    var postId = ""

    private val _helper = SQLiteHelper(this@SpotMainActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.spotmain)

        //postId = intent.getStringExtra("Post_Id")
        //postId = "92"
        swipelayout2.setColorSchemeResources(R.color.colorMain)

        SpotPhotoGet().execute()
        val address = getAddress(latitude, longitude)
        SpotCode.setText(getAddress(latitude, longitude))

        swipelayout2.setOnRefreshListener {
            val address = getAddress(latitude, longitude)
            print(address)
            SpotPhotoGet().execute()
        }

    }

    //位置情報を住所に変換する関数
    private fun getAddress(lat: Double, lng: Double): String {
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocation(lat, lng, 1)
        //println("0" + list[0].getAddressLine(0))
        var kekka = list[0].getAdminArea() + list[0].getLocality() + list[0].getThoroughfare() +list[0].getSubThoroughfare()
        return kekka
    }

    private inner class SpotPhotoGet() : AsyncTask<String, String, String>(){

        override fun doInBackground(vararg params: String?): String {

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
            val url = "https://encount.cf/encount/SpotInfoSend.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            println("経度"+latitude.toString())
            println("緯度"+longitude.toString())
            println("ユーザ"+id)

            //Formに要素を追加
            formBuilder.add("latitude", latitude.toString())
            formBuilder.add("longitude", longitude.toString())
            formBuilder.add("user",id)

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
                            i.imageLng,
                            i.postId,
                            i.likeFlag
                        )
                    )
                }

                SpotPopularCount.text = Integer.toString(postCount)
                SpotNewCount.text = Integer.toString(postCount)
                //print(Integer.toString(postCount))
                gridview.adapter = GridAdapter(this@SpotMainActivity, postList)
                swipelayout2.isRefreshing = false
            }
            catch (e : Exception){

            }
        }
    }
    override fun onDestroy(){

        //ヘルパーオブジェクトの開放
        _helper!!.close()
        super.onDestroy()
    }
}