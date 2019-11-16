package com.example.encount

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class UserProfileChange : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_change)

        startActivity(Intent(this, UserLogin::class.java))
    }
}
