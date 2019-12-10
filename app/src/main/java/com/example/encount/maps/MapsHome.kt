package com.example.encount.maps

import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import com.example.encount.MapsDataClassList
import com.example.encount.MapsList
import com.example.encount.PostList

import com.example.encount.R
import com.example.encount.post.UserHome
import com.example.encount.user.UserProfile
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MapsHome : FragmentActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mMap: GoogleMap? = null
    private val requestingLocationUpdates = true //フラグ
    private val locationRequest: LocationRequest = LocationRequest.create()
    private var postList = mutableListOf<MapsList>()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_home)
        MapPostGet().execute()

        //画面遷移用
        val menuHomeBtn = findViewById<LinearLayout>(R.id.MenuHome)
        val menuUserBtn = findViewById<LinearLayout>(R.id.MenuUser)
        val spotInfoBtn = findViewById<Button>(R.id.button2)

        //メニューバーを押した場合の処理
        menuUserBtn.setOnClickListener {
            startActivity(Intent(this, UserProfile::class.java))
            overridePendingTransition(0, 0)
        }

        menuHomeBtn.setOnClickListener {

            startActivity(Intent(this, UserHome::class.java))
            overridePendingTransition(0, 0)
        }

        spotInfoBtn.setOnClickListener {

            //startActivity(Intent(this, SpotInfo::class.java))
            overridePendingTransition(0, 0)
        }
        // Android 6, API 23以上でパーミッションの確認
        if (Build.VERSION.SDK_INT >= 23) {
            val permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            checkPermission(permissions, REQUEST_CODE)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
                    Log.d("debug", "緯度" + location.latitude)
                    Log.d("debug", "経度" + location.longitude)
                }
            }
        }
    }

    fun setPostList(postList: MutableList<MapsList>) {
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
        val spot = LatLng(35.7044997, 139.9843911)
        mMap!!.addMarker(
            MarkerOptions()
                .position(spot)
                .title("Marker in FJB")
                .icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.smile1)
                )
        )
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(spot))
        //マップのズーム絶対値指定　1: 世界 5: 大陸 10:都市 15:街路 20:建物 ぐらいのサイズ
        mMap!!.moveCamera(CameraUpdateFactory.zoomTo(19f))
    }

    //許可されていないパーミッションリクエスト
    fun checkPermission(permissions: Array<String>, request_code: Int) {
        ActivityCompat.requestPermissions(this, permissions, request_code)
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
        fun setPostList(mapsHome: MapsHome, mutableList: MutableList<MapsList>) {
            mapsHome.postList = mapsHome.postList
            Log.d("debug", "pass" + mapsHome.postList[1].imgpath)
        }
    }

    //インナークラス
    inner class MapPostGet() : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String): String {
            Log.d("debug", "background")
            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://kinako.cf/encount/MapsDataGet.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //リクエストの内容にformを追加
            val form = formBuilder.build()

            //リクエストを生成
            val request = Request.Builder().url(url).post(form).build()

            try {
                //受信用
                val response = client.newCall(request).execute()
                return response.body()!!.string()
            } catch (e: IOException) {
                e.printStackTrace()
                return "Error"
            }
        }


        //結果表示
        override fun onPostExecute(result: String) {
            try {
                //var postList = mutableListOf<MapsList>()
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
                setPostList(postList)
            } catch (e: Exception) {
            }
        }
    }
}