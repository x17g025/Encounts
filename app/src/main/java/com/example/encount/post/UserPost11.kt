package com.example.encount.post

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.encount.NavigationActivity
import com.example.encount.R
import com.example.encount.SQLiteHelper
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_user_post.*


/**
 * 投稿機能(カメラ、コメント、位置情報)
 * 制作者：大野
 */

class UserPost11 : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val requestingLocationUpdates = true //フラグ
    private val locationRequest: LocationRequest = LocationRequest.create()
    val _helper = SQLiteHelper(this@UserPost11)
    //写真のパスを受け取る変数(将来的には撮影した写真のパス、ファイル名を取得して指定する)
    var uurl = ""
    //緯度
    var lat = ""
    //経度
    var lng = ""
    //送信するコメント内容の受け取り変数
    var cmnt = ""

    //保存された画像のURI
    private var _imageUri: Uri? = null
    //緯度フィールド
    private var _latitude = 0.0
    //経度フィールド
    private var _longitude = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_post)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest.setInterval(10000)   //最遅の更新間隔
        locationRequest.setFastestInterval(5000)   //最速の更新間隔
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)           //バッテリー消費を抑えたい場合、精度は100m程度
        onResume()

        /**
         * 投稿処理
         * 投稿ボタン押すと動作する
         */
/*
    val commentInput = findViewById<EditText>(R.id.commentInput)
    ivCameraBig.visibility = View.GONE
    postClose.setOnClickListener {
        startActivity(Intent(this, NavigationActivity::class.java))
    }

    ivCamera.setOnClickListener {
        ivCamera.visibility = View.GONE
        ivCameraBig.visibility = View.VISIBLE
    }

    ivCameraBig.setOnClickListener {
        ivCamera.visibility = View.VISIBLE
        ivCameraBig.visibility = View.GONE
    }
*/
        // 投稿ボタンが押された時
        postButton.setOnClickListener {
            if(_imageUri != null) {
                //パスの処理
                val uuri = getFileSchemeUri(_imageUri as Uri)

                println(uuri.toString())

                var pass = uuri.toString().substring(uuri.toString().length - 17)
                print(pass)
                uurl = pass

                //コメントをEditTextから取得
                cmnt = commentInput.getText().toString()
                //緯度を取得
                latitude = _latitude.toString()
                //経度を取得
                longitude = _longitude.toString()

                //ここで現在地取得処理(更新)を終了させる
                print("GPS終了")
                locationManager.removeUpdates(locationListener)

                //投稿処理開始
                val postTask = OkHttpPost()
                postTask.execute(/*uuri.toString()*/)

                SweetAlertDialog(this@UserPost, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("投稿完了")
                    .setContentText("")
                    .setConfirmText("ホーム画面へ")
                    .setConfirmClickListener {
                            sDialog -> sDialog.dismissWithAnimation()
                        goHome()
                    }
                    .show()
            }
            else{

            }
        }


    }

    //Update Result
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations) {
                if (location != null) {
                    _latitude = location.latitude   //test
                    Log.d("debug", "緯度" + location.latitude)
                    Log.d("debug", "経度" + location.longitude)
                }
            }
        }
    }
    //start locationUpdate
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
    //stop locationUpdate
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
