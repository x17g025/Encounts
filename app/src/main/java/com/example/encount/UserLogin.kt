package com.example.encount

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_user_login.*

class UserLogin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)


        loginbtn.setOnClickListener {

            val userMail = findViewById<EditText>(R.id.usermail)
            val userPass = findViewById<EditText>(R.id.userpass)

            DataPost.mailadress = userMail.getText().toString()
            DataPost.password   = userPass.getText().toString()

            val postTask = DataPost()
            var loginFlag = postTask.execute()
        }
    }
}
