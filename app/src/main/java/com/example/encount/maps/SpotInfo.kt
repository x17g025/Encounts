package com.example.encount.maps

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.encount.R
import com.example.encount.post.UserHome
import com.example.encount.user.UserProfile

/**
 * スポット詳細表示画面
 * このクラスでは、マップ上で任意のスポットをタップした場合に遷移する画面。
 */

class SpotInfo  : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_spotinfo)

        //画面遷移用
        val menuHomeBtn = findViewById<LinearLayout>(R.id.MenuHome)
        val menuUserBtn = findViewById<LinearLayout>(R.id.MenuUser)
        val SpotPopular = findViewById<LinearLayout>(R.id.SpotPopular)

        //メニューバーを押した場合の処理
        menuUserBtn.setOnClickListener {

            startActivity(Intent(this, UserProfile::class.java))
            overridePendingTransition(0, 0)
        }

        menuHomeBtn.setOnClickListener {

            startActivity(Intent(this, UserHome::class.java))
            overridePendingTransition(0, 0)
        }

        SpotPopular.setOnClickListener {

            startActivity(Intent(this, UserProfile::class.java))
            overridePendingTransition(0, 0)
        }
    }
}