package com.example.encount.maps

import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.encount.MapsDataClassList
import com.example.encount.MapsList
import com.example.encount.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_maps_home.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MapsHome : Fragment(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mMap: GoogleMap? = null
    private val requestingLocationUpdates = true //フラグ
    private val locationRequest: LocationRequest = LocationRequest.create()
    private var postList = mutableListOf<MapsList>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.activity_maps_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //非同期処理実行
        MapPostGet(this).execute()

        val mapFragment: SupportMapFragment = getChildFragmentManager().findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        button2.setOnClickListener {

            //startActivity(context(this, SpotInfo::class.java))
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
                    Log.d("debug", "緯度" + location.latitude)
                    Log.d("debug", "経度" + location.longitude)
                }
            }
        }
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
    //setter
    fun setPostList(postList : MutableList<MapsList>){
        this.postList = postList
        Log.d("debug", "pass" + this.postList[0].imgpath)
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
    }
}