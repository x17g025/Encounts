package com.example.encount

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import kotlinx.android.synthetic.main.activity_navigation.*
import androidx.core.view.GravityCompat
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.encount.user.UserProfile
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception
import android.widget.TextView
import android.view.LayoutInflater

/**
 * やってること
 * ログインしてたらホーム画面に、してなければログイン画面に飛ばす
 *
 * 製作者：中村
 */

class NavigationActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

    private val _helper = SQLiteHelper(this@NavigationActivity)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        UserDataGet()

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

    private inner class UserDataGet() : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            var id     = ""
            val db     = _helper.writableDatabase
            val sql    = "select * from userInfo"
            val cursor = db.rawQuery(sql, null)

            while(cursor.moveToNext()){

                val idxId = cursor.getColumnIndex("user_id")
                id = cursor.getString(idxId)
            }

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://kinako.cf/encount/UserDataGet.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("id", id)
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
                val headerView = LayoutInflater.from(this@NavigationActivity).inflate(R.layout.drawer_navigation_header, navigationView, false)
                navigationView.addHeaderView(headerView)
                val Name = headerView.findViewById<TextView>(R.id.ToolUserName)
                Name.text= userData.userName
                val Number = headerView.findViewById<TextView>(R.id.ToolUserNumber)
                Number.text = userData.userNumber.toString()
            }
            catch(e : Exception){
            }

        }
    }
}