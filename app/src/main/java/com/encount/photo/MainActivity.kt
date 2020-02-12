package com.encount.photo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.app.ActivityCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.encount.photo.user.UserSingin
import java.lang.Exception
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri


/**
 * やってること
 * ログインしてたらホーム画面に、してなければログイン画面に飛ばす
 *
 * 製作者：中村
 */

class MainActivity : AppCompatActivity() {

    private val _helper = SQLiteHelper(this@MainActivity)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_main)

        // Android 6, API 23以上でパーミッションの確認
        if (Build.VERSION.SDK_INT >= 23) {
            val permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE
            )
            checkPermission(permissions, REQUEST_CODE)
        }
        //チュートリアルを表示
        //初回起動時のみ表示
        //TutorialActivity.showIfNeeded(this@MainActivity,savedInstanceState)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == REQUEST_PERMISSION) {

            var checkFlag = 0

            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("PermissionPer", "Added Permission: " + permissions[i])

                    checkFlag++

                    if(checkFlag == 4){

                        transAct()
                    }
                }
                else {
                    // パーミッションが拒否された
                    Log.d("PermissionPer", "Rejected Permission: " + permissions[i])

                    try {
                        SweetAlertDialog(this@MainActivity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("権限エラー")
                            .setContentText("権限が取得できません。")
                            .setConfirmText("設定へ")
                            .setConfirmClickListener { sDialog ->
                                sDialog.dismissWithAnimation()
                                startActivity( Intent(ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")))
                            }
                            .show()
                    }
                    catch (e : Exception){

                    }
                }
            }
        }
    }

    //許可されていないパーミッションリクエスト
    fun checkPermission(permissions: Array<String>, request_code: Int) {

        ActivityCompat.requestPermissions(this, permissions, request_code)
    }

    companion object {

        private val REQUEST_CODE = 1000
        private val REQUEST_PERMISSION = 1000
    }

    fun transAct(){

        val db = _helper.writableDatabase
        val sql = "select * from userInfo"
        val cursor = db.rawQuery(sql, null)
        var userId = ""

        while(cursor.moveToNext()){

            val idxId = cursor.getColumnIndex("user_id")
            userId = cursor.getString(idxId)
        }

        Handler().postDelayed({
            when {

                userId == "" || userId == null  -> startActivity(Intent(this, UserSingin::class.java))
                else            -> startActivity(Intent(this, NavigationActivity::class.java))
            }
            finish()
        }, 1500)
    }

    override fun onDestroy(){

        //ヘルパーオブジェクトの開放
        _helper.close()
        super.onDestroy()
    }
}