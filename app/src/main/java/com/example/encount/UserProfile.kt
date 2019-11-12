package com.example.encount

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class UserProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        //val profilechange = findViewById<Button>(R.id.prfCng)
        prfCng.setOnClickListener {

            //3.Intentクラスのオブジェクトを生成。
            val intent = Intent(this, UserProfileChange::class.java)
            //生成したオブジェクトを引数に画面を起動！
            startActivity(intent)
        }
    }
}
