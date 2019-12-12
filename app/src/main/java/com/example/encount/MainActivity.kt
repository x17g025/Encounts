package com.example.encount

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import com.example.encount.user.UserLogin

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
        setContentView(R.layout.activity_main)

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

                userId == "" || userId == null  -> startActivity(Intent(this, UserLogin::class.java))
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