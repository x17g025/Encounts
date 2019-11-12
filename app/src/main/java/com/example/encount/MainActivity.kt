package com.example.encount

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val _helper = SQLiteHelper(this@MainActivity)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val db = _helper.writableDatabase
        val sql = "select * from userLogin;"
        val cursor = db.rawQuery(sql, null)
        var userId = ""

        while(cursor.moveToNext()){

            val idxId = cursor.getColumnIndex("userId")
            userId = cursor.getString(idxId)
        }

        when {

            userId == null  -> startActivity(Intent(this, UserLogin::class.java))
            else            -> startActivity(Intent(this, UserProfile::class.java))
        }
    }

    override fun onDestroy(){

        //ヘルパーオブジェクトの開放
        _helper.close()
        super.onDestroy()
    }
}