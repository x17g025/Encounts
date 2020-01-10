package com.example.encount.user

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.encount.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_user_profile.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception

/**
 * やってること
 * ユーザのプロフィールを表示する
 *
 * 製作者：中村
 */

class UserProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        UserDataList.adapter = TabAdapter(supportFragmentManager, this)
        //TabLayoutにViewPagerを設定
        tabLayout.setupWithViewPager(UserDataList)
    }
}
