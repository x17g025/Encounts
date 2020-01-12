package com.example.encount.post

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.encount.R
import kotlinx.android.synthetic.main.activity_post_details.*

/**
 * やってること
 * 投稿の詳細な情報を表示する
 *
 * 製作者：中村
 */

class PostDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        val postId = intent.getStringExtra("Post_Id")

        tvUserName.text = postId
    }
}