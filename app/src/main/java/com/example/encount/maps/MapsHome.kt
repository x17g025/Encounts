package com.example.encount.maps

import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.encount.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task

class MapsHome : Fragment() {

   // private var fusedLocationClient: FusedLocationProviderClient? = null
    //private var location: Location? = null
    //private var toast: Toast? = null //デバック用
    //private var mMap: GoogleMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return  inflater.inflate(R.layout.activity_maps_home, container, false)

        /*
        // Android 6, API 23以上でパーミッションの確認
        if (Build.VERSION.SDK_INT >= 23) {
            val permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            checkPermission(permissions, REQUEST_CODE)
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationRequest = LocationRequest()

        //どれか一つを選択
        locationRequest.setPriority(
            //              LocationRequest.PRIORITY_HIGH_ACCURACY);  //高精度の位置情報を取得
            LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        )  //バッテリー消費を抑えたい場合、精度は100m程度
        //              LocationRequest.PRIORITY_LOW_POWER);              //バッテリー消費を抑えたい場合、精度は10km
        //              LocationRequest.PRIORITY_NO_POWER);               //位置情報取得をアプリが自ら測位しない

        getLastLocation()*/
    }
    /*
    //最新の位置情報の取得(nullが返ってくる可能性)
    fun getLastLocation() {
        fusedLocationClient!!.getLastLocation().addOnCompleteListener(
            this@MapsHome, OnCompleteListener<Location> {
                fun onCompleteLisner(task: Task<Location>) {
                    //アクセスが成功したら
                    if (task.isSuccessful() && task.getResult() != null) {
                        location = task.getResult()
                        toast = Toast.makeText(
                            this@MapsHome,
                            "緯度" + location!!.latitude + "\n" + "経度" + location!!.longitude,
                            Toast.LENGTH_LONG
                        )
                        //toast.show()
                        Log.d("debug", "計測成功")
                    }
                    else {
                        toast = Toast.makeText(this@MapsHome, "計測不能", Toast.LENGTH_LONG)
                        //toast.show()
                        Log.d("debug", "計測失敗")
                    }
                }
            }
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Log.d("debug", "計測開始")
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
        mMap!!.moveCamera(CameraUpdateFactory.zoomTo(15f))

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
    }*/
}
