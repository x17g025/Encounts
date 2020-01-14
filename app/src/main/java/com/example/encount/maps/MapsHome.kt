package com.example.encount.maps

import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.example.encount.MapsDataClassList
import com.example.encount.MapsList
import com.example.encount.PostList2
import com.example.encount.R
import com.example.encount.post.UserPost
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.zzaa
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_maps_home.*
import kotlinx.android.synthetic.main.grid_items.*
import kotlinx.android.synthetic.main.grid_items.view.*
import kotlinx.android.synthetic.main.spotmain.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception

class MapsHome : Fragment(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mMap: GoogleMap? = null
    private val requestingLocationUpdates = true //フラグ
    private val locationRequest: LocationRequest = LocationRequest.create()
    //private var postList = mutableListOf<MapsList>()
    private var postList = mutableListOf<PostList2>()
    //取得した写真の件数を格納する
    private var cnt = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.activity_maps_home, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //MapsPostGetを実行
        //MapPostGet(this).execute()

        val mapFragment: SupportMapFragment = getChildFragmentManager().findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        button2.setOnClickListener {

            //スポット詳細画面へ遷移
            val intent = Intent(context, SpotMainActivity::class.java)
            startActivity(intent)
        }

        // Android 6, API 23以上でパーミッションの確認
        if (Build.VERSION.SDK_INT >= 23) {
            val permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            checkPermission(permissions, REQUEST_CODE)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        locationRequest.setInterval(10000)   //最遅の更新間隔
        locationRequest.setFastestInterval(5000)   //最速の更新間隔
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)           //バッテリー消費を抑えたい場合、精度は100m程度
        onResume()
    }

    //Update Result
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations) {
                if (location != null) {

                    //ここで前回のマップのピンを全削除する処理

                    //グローバル変数に位置情報を代入
                    latitude = location.latitude
                    longitude = location.longitude

                    //MapPostGet(this,lat,lng).execute()で緯度経度を引数にして渡す
                    //MapPostGet(this@MapsHome).execute()
                    //サーバと通信する処理（インナークラス）を呼び出して実行する
                    SpotPhotoGet(this@MapsHome).execute()

                    Log.d("debug", "現在地の緯度" + location.latitude)
                    Log.d("debug", "現在地の経度" + location.longitude)

                    //写真が１件以上あれば、マップのピンを立てる処理を行う
                    if(cnt >= 1){

                        //サーバから取得した1番目の写真の位置情報をデバッグ表示
                        //Log.d("debug","postList[0].imageLat : " + postList[0].imageLat)
                        //Log.d("debug","postList[0].imageLng : " + postList[0].imageLng)

                        //下のfor文内で使うカウント変数
                        var ccnt = 0

                        Log.d("debug","取得した写真の件数 : " + cnt)

                        //取得した写真の件数分ピンを打つ処理
                        for(i in postList){
                            val spot = LatLng(postList[ccnt].imageLat.toDouble(),postList[ccnt].imageLng.toDouble())
                            println("imageID : " + postList[ccnt].imageId)
                            Glide.with(activity)
                                .asBitmap()
                                .load(postList[ccnt].imagePath)
                                .into(object : SimpleTarget<Bitmap>(100,100) {

                                    //正常に写真取得できればピンを打つ
                                    override fun onResourceReady(
                                        resource: Bitmap?,
                                        transition: Transition<in Bitmap>?
                                    ) {
                                        mMap!!.addMarker(
                                            MarkerOptions()
                                                .position(spot)
                                                    //現状だと、
                                                .title("imageID : " + postList[0].imageId)
                                                .icon(BitmapDescriptorFactory.fromBitmap(resource))
                                        )
                                    }

                                    override fun onLoadFailed(errorDrawable: Drawable?) {
                                        mMap!!.addMarker(
                                            MarkerOptions()
                                                .position(spot)
                                                .title("エラーで写真を正しく表示できませんでした。")
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.error))
                                        )
                                    }

                                })

                            ccnt++
                        }
                    }
                }
            }
        }
    }

    /*fun setPostList(postList: MutableList<MapsList>) {
        this.postList = postList
    }*/
    fun setPostList(postList: MutableList<PostList2>) {
        this.postList = postList
    }

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) startLocationUpdates()
    }

    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback, null /* Looper */
        )
        /*
              startActivity(Intent(this, UserHome::class.java))
              overridePendingTransition(0, 0)
          }*/
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    //default location
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        /*val spot = LatLng(35.7044997, 139.9843911)
        mMap!!.addMarker(
            MarkerOptions()
                .position(spot)
                .title("Marker in FJB")
                .icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.smile1)
                )
        )*/
        //マップ上に現在地を表示
        mMap!!.setMyLocationEnabled(true)
        //mMap!!.moveCamera(CameraUpdateFactory.newLatLng(spot))
        //マップのズーム絶対値指定　1: 世界 5: 大陸 10:都市 15:街路 20:建物 ぐらいのサイズ
        mMap!!.moveCamera(CameraUpdateFactory.zoomTo(19f))

        //タップしたときのリスナーをセット
        mMap!!.setOnMapClickListener {
            mMap!!.moveCamera(CameraUpdateFactory.zoomTo(20f))

        }
    }

    //許可されていないパーミッションリクエスト
    fun checkPermission(permissions: Array<String>, request_code: Int) {
        ActivityCompat.requestPermissions(activity!!, permissions, request_code)
    }

    //結果
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION) {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    mMap!!.setMyLocationEnabled(true)
                    Log.d("Permission", "Added Permission: " + permissions[i])
                } else {
                    // パーミッションが拒否された
                    Log.d("Permission", "Rejected Permission: " + permissions[i])
                }
            }
        }
    }

    companion object {
        private val REQUEST_CODE = 1000
        private val REQUEST_PERMISSION = 1000
        //setter
        /*fun setPostList(mapsHome: MapsHome, mutableList: MutableList<MapsList>) {
            mapsHome.postList = mapsHome.postList
            Log.d("debug", "pass" + mapsHome.postList[0].imgpath)
        }*/
        fun setPostList(mapsHome: MapsHome, mutableList: MutableList<PostList2>) {
            mapsHome.postList = mapsHome.postList
            Log.d("debug", "pass" + mapsHome.postList[0])
        }
    }

    /**
     * ここから下はサーバに現在地を表示し、現在地周辺の写真を取得する処理
     *
     */

    private inner class SpotPhotoGet(val activity: MapsHome) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String?): String {
            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/SpotInfoSend.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            println("サーバに送信する緯度：" + latitude.toString())
            println("サーバに送信する経度：" + longitude.toString())

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
                //println(response.body()!!.string())
                return response.body()!!.string()
            } catch (e: IOException) {
                e.printStackTrace()
                return "Error"
            }
        }

        override fun onPostExecute(result: String) {
            try {
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

                cnt = postCount
                activity.setPostList(postList)

            } catch (e: Exception) {

            }
        }
    }

}