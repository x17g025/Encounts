package com.example.encount

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import kotlinx.android.synthetic.main.activity_navigation.*
import androidx.core.view.GravityCompat
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.encount.user.UserProfile
import com.google.android.material.navigation.NavigationView

/**
 * やってること
 * ログインしてたらホーム画面に、してなければログイン画面に飛ばす
 *
 * 製作者：中村
 */

class NavigationActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

       this.setDrawerLayout()

        val navController = findNavController(R.id.nav_host_fragment)
        setupWithNavController(bottom_navigation, navController)
    }

    private fun setDrawerLayout(){

        val toggle = ActionBarDrawerToggle(Activity(), drawer_layout, toolbar, R.string.nav_open, R.string.nav_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_user -> startActivity(Intent(this, UserProfile::class.java))
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}