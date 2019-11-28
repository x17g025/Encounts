package com.example.encount

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import kotlinx.android.synthetic.main.activity_navigation.*
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.Navigation
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.encount.post.UserHome
import com.example.encount.user.UserLogin
import com.example.encount.user.UserProfile


/**
 * やってること
 * ログインしてたらホーム画面に、してなければログイン画面に飛ばす
 *
 * 製作者：中村
 */

class NavigationActivity : AppCompatActivity() {

    var mAppBarConfiguration : AppBarConfiguration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        val navController = findNavController(R.id.nav_host_fragment)

        setSupportActionBar(toolbar)

        mAppBarConfiguration = AppBarConfiguration.Builder(
                R.id.navi_settings
                ).setDrawerLayout(drawer_layout).build()

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration!!)
        setupWithNavController(nav_view, navController)
        setupWithNavController(bottom_navigation, navController)

        displaySelectedScreen(R.id.navi_settings)
    }

    override fun onSupportNavigateUp(): Boolean {
        //AndroidXライブラリのナビゲーションコントローラオブジェクトを取得する。第2引数はナビゲーションホストのID
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        //たぶん、アップ処理し、成否を返す
        return (NavigationUI.navigateUp(
            navController,
            mAppBarConfiguration!!
        ) //NavControllerにアップ処理をさせる。アップ処理が行われたらtrueが返る
                || super.onSupportNavigateUp()) //不明。既定のアップ処理？
    }

    fun displaySelectedScreen(itemId: Int) {

        when (itemId) {
            R.id.navi_settings -> Intent(this, UserLogin::class.java)
        }

        drawer_layout.closeDrawer(GravityCompat.START)
    }
}