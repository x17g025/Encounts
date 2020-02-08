package com.encount.photo

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import kotlinx.android.synthetic.main.activity_nav_main.*
import androidx.core.view.GravityCompat
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.encount.photo.user.UserProfile
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception
import com.encount.photo.user.UserSettings
import kotlinx.android.synthetic.main.activity_nav_header.*
import kotlinx.android.synthetic.main.activity_nav_header.view.*

/**
 * やってること
 * ログインしてたらホーム画面に、してなければログイン画面に飛ばす
 *
 * 製作者：中村
 */

class NavigationActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

    var inId = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_main)

        inId = doSelectSQLite(this)

        this.setDrawerLayout()

        val navController = findNavController(R.id.nav_host_fragment)
        setupWithNavController(bottom_navigation, navController)

        UserDataGet().execute()
    }

    override fun onStart() {

        super.onStart()
        UserDataGet().execute()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            SweetAlertDialog(this@NavigationActivity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("終了")
                .setContentText("アプリを終了しますか？")
                .setConfirmText("Yes")
                .setConfirmClickListener {
                        sDialog ->  sDialog.dismissWithAnimation()
                                    this.finish()
                                    this.moveTaskToBack(true)
                }
                .setCancelButton(
                    "No"
                ) { sDialog -> sDialog.dismissWithAnimation() }
                .show()
            return true
        }
        return false
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
            R.id.nav_settings -> startActivity(Intent(this, UserSettings::class.java))
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return false
    }

    inner class UserDataGet : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/UserDataGet.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("id", inId)
            //リクエストの内容にformを追加
            val form = formBuilder.build()

            //リクエストを生成
            val request = Request.Builder().url(url).post(form).build()

            try {
                val response = client.newCall(request).execute()
                return response.body()!!.string()
            } catch (e: IOException) {
                e.printStackTrace()
                return "Error"
            }
        }

        override fun onPostExecute(result: String) {

            try{

                val userData = Gson().fromJson(result, UserDataClassList::class.java)
                val navigationView = findViewById<NavigationView>(R.id.nav_view)
                val headerView = navigationView.getHeaderView(0)

                Glide.with(this@NavigationActivity).load(userData.userIcon).into(ivUserIcon)
                headerView.navUserName.text   = userData.userName
                headerView.navUserNumber.text = "ID : " + userData.userNumber.toString()
            }
            catch(e : Exception){
            }
        }
    }
}