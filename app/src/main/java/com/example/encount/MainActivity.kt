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

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //val profilechange = findViewById<Button>(R.id.prfCng)
        prfCng.setOnClickListener {

            //3.Intentクラスのオブジェクトを生成。
            val intent = Intent(this, UserProfileChange::class.java)
            //生成したオブジェクトを引数に画面を起動！
            startActivity(intent)
        }
    }
}