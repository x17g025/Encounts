package com.example.encount

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

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
                else            -> startActivity(Intent(this, UserHome::class.java))
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