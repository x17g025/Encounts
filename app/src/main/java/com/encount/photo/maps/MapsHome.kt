package com.encount.photo.maps

import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.encount.photo.MapPostData
import com.encount.photo.PostDataClassList
import com.encount.photo.R
import com.encount.photo.post.PostDetails
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.ui.IconGenerator
import kotlinx.android.synthetic.main.fragment_maps_home.*
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
    private var postList = mutableListOf<MapPostData>()
    //取得した写真の件数を格納する
    private var cnt = 0
    //マップ上に打ったピンを管理するためのList
    val mMarkers = mutableListOf<Marker>()
    var mMarkersOld = mutableListOf<Marker>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_maps_home, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val mapFragment: SupportMapFragment =
            getChildFragmentManager().findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        ivSpotPost.setOnClickListener {

            //スポット詳細画面へ遷移
            val intent = Intent(context, SpotMainActivity::class.java)
            startActivity(intent)
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
                            //前回打ったピンを全てOldに移動
                            mMarkersOld = mMarkers.toMutableList()

                            //グローバル変数に位置情報を代入
                            latitude = location.latitude
                            longitude = location.longitude

                            ivSpotPost.visibility = View.VISIBLE

                            //MapPostGet(this,lat,lng).execute()で緯度経度を引数にして渡す
                            //MapPostGet(this@MapsHome).execute()
                            //サーバと通信する処理（インナークラス）を呼び出して実行する
                            SpotPhotoGet(this@MapsHome).execute()
                            println("取得した件数"+cnt)

                            mMap!!.setMyLocationEnabled(true)
                            //mMap!!.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latitude, longitude)))

                            val camPos = CameraPosition.Builder()
                                .target(LatLng(latitude, longitude)) // Sets the new camera position
                                .zoom(19f) // Sets the zoom
                                .bearing(0f) // Rotate the camera
                                .tilt(40f) // Set the camera tilt
                                .build()
                            mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(camPos))

                            //座標から住所変換のテスト
                            val geocoder = Geocoder(context)
                            //val addressList: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
                            val addressList: List<Address>? =
                                geocoder.getFromLocation(latitude, longitude, 1)
                            val adminArea = addressList?.first()!!.adminArea
                            println(adminArea)

                            //マップの移動範囲を制限
                            var maxLat = latitude + 0.001
                            var maxLng = longitude + 0.002
                            var minLat = latitude - 0.001
                            var minLng = longitude - 0.002
                            mMap!!.setLatLngBoundsForCameraTarget(
                                LatLngBounds(LatLng(minLat,minLng),LatLng(maxLat,maxLng)))

                            //写真が１件以上あれば、マップのピンを立てる処理を行う
                            if (cnt >= 1) {
                                //取得した写真の件数分ピンを打つ処理

                                for (i in 0..cnt - 1) {
                                    val spot = LatLng(
                                        postList[i].imageLat.toDouble(),
                                        postList[i].imageLng.toDouble()
                                    )

                                    Glide.with(activity)
                                        .asBitmap()
                                        .load(postList[i].imagePath)
                                        .into(object : SimpleTarget<Bitmap>(200, 200) {

                                            //正常に写真取得できればピンを打つ
                                            override fun onResourceReady(
                                                resource: Bitmap?,
                                                transition: Transition<in Bitmap>?
                                            ) {
                                                //iconGeneratorを使用
                                                val iconGenerator = IconGenerator(activity)
                                                val imageView = ImageView(activity)
                                                imageView.setImageBitmap(resource)
                                                iconGenerator.setContentView(imageView)

                                                //ピンを打ちつつ、Listにも追加
                                                mMarkers.add(mMap!!.addMarker(
                                                    MarkerOptions()
                                                        .position(spot)
                                                        .title(postList[i].postId)
                                                        .snippet(postList[i].userId)
                                                        //.icon(BitmapDescriptorFactory.fromBitmap(resource))
                                                        .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon()))
                                                ))
                                            }
                                        })
                                }
                                //ピンが打ち終わったら、前回打ったピンを全削除する
                                //この処理をしないでピンを全削除すると、ピンが点滅してしまう
                                if(mMarkersOld.size >= 1){
                                    val itr = mMarkersOld.iterator()
                                    while (itr.hasNext()){
                                        val m: Marker = itr.next()
                                        m.remove()//地図上から削除
                                        itr.remove()//リストからも削除
                                    }
                                }
                    }
                }
            }
        }
    }

    fun setPostList(postList: MutableList<MapPostData>) {
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
        //マップのスタイルも変えられるようにしたい
        //mMap!!.setMapStyle(GoogleMap.MAP_TYPE_TERRAIN)
        //移動
        googleMap.uiSettings.isScrollGesturesEnabled = true
        //ズーム
        googleMap.uiSettings.isZoomGesturesEnabled = true
        //回転
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isRotateGesturesEnabled = true
        //ティルト 2本指スワイプで視点を傾けることができる
        googleMap.uiSettings.isTiltGesturesEnabled = false
        //ズーム範囲指定
        googleMap.setMaxZoomPreference(20f)
        googleMap.setMinZoomPreference(17f)

        mMap!!.setOnMarkerClickListener { marker ->
            val intent = Intent(context, PostDetails::class.java)
            intent.putExtra("Post_Id",marker.title)
            intent.putExtra("User_Id",marker.snippet)
            intent.putExtra("Pre_Act", "map")
            startActivity(intent)
            true
        }
    }

    companion object {

        private val REQUEST_CODE = 1000
        private val REQUEST_PERMISSION = 1000
    }

    /**
     * ここから下はサーバに現在地を表示し、現在地周辺の写真を取得する処理
     */

    private inner class SpotPhotoGet(val activity: MapsHome) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String?): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/SpotInfoSend.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            println("経度" + latitude.toString())
            println("緯度" + longitude.toString())

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
                var postList = mutableListOf<MapPostData>()
                val listType = object : TypeToken<List<PostDataClassList>>() {}.type
                val postData = Gson().fromJson<List<PostDataClassList>>(result, listType)
                var postCount = 0

                for (i in postData) {

                    postCount++

                    postList.add(
                        MapPostData(
                            i.postId,
                            i.userId,
                            i.postImage,
                            i.imageLat.toString(),
                            i.imageLng.toString(),
                            i.postLikeCnt.toInt()
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
