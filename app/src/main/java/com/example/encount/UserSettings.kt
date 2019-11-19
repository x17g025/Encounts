package com.example.encount

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_user_settings.*


class UserSettings : AppCompatActivity() {

    private val _helper = SQLiteHelper(this@UserSettings)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_settings)

        logoutbtn.setOnClickListener {

            val db = _helper.writableDatabase
            val sqlDelete = "delete from userInfo"
            var stmt = db.compileStatement(sqlDelete)
            stmt.executeUpdateDelete()

            val intent = Intent(this, UserLogin::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}
