package com.example.encount

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_user_profile_change.*

class UserSettings : AppCompatActivity() {

    private val _helper = SQLiteHelper(this@UserSettings)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_change)

        logoutbtn.setOnClickListener {

            val db = _helper.writableDatabase
            val sqlDelete = "delete from userInfo"
            var stmt = db.compileStatement(sqlDelete)
            stmt.executeUpdateDelete()

            startActivity(Intent(this, UserLogin::class.java))
        }
    }
}
