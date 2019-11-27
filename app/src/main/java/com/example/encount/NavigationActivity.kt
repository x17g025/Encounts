package com.example.encount

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.encount.friend.FriendAdd
import com.example.encount.maps.MapsHome
import com.example.encount.post.UserHome
import com.example.encount.user.UserProfile
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_navigation.*

/**
 * やってること
 * ログインしてたらホーム画面に、してなければログイン画面に飛ばす
 *
 * 製作者：中村
 */

class NavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            val transaction = supportFragmentManager.beginTransaction()
            when(item.itemId) {
                R.id.home -> {
                    transaction.replace(R.id.nav_host_fragment, UserHome()).commit()
                }
                R.id.map -> {
                    transaction.replace(R.id.nav_host_fragment, MapsHome()).commit()
                }
                R.id.search -> {
                    transaction.replace(R.id.nav_host_fragment, FriendAdd()).commit()
                }
                R.id.user -> {
                    transaction.replace(R.id.nav_host_fragment, UserProfile()).commit()
                }
            }
            true
        }

        supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, UserHome()).commit()
    }


}