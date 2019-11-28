package com.example.encount

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import kotlinx.android.synthetic.main.activity_navigation.*
import android.R

/**
 * やってること
 * ログインしてたらホーム画面に、してなければログイン画面に飛ばす
 *
 * 製作者：中村
 */

class NavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(com.example.encount.R.layout.activity_navigation)

        val navController = findNavController(com.example.encount.R.id.nav_host_fragment)
        setupWithNavController(bottom_navigation, navController)
    }
}