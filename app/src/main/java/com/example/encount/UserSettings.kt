package com.example.encount

import android.app.LauncherActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_user_profile_change.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



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

            val intent = Intent(this, UserLogin::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}
