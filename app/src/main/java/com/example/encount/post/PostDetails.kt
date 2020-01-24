package com.example.encount.post


import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.encount.*
import com.example.encount.maps.latitude
import com.example.encount.maps.longitude
import com.example.encount.user.UserLogin
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_post_details.*
import kotlinx.android.synthetic.main.activity_user_home.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception

/**
 * やってること
 * 投稿の詳細な情報を表示する
 *
 * 画像
 * ユーザーネーム
 * アイコン
 * いいねフラグ
 * テキスト
 * 投稿日時
 *
 * 製作者：中村
 */

class PostDetails : AppCompatActivity() {

    var postId = ""

    private val _helper = SQLiteHelper(this@PostDetails)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        postId = intent.getStringExtra("Post_Id")

        UserPostGet().execute()
        UserReplyGet().execute()

        //タップで投稿の詳細画面へ
        ivPostLike.setOnClickListener{

            UserPostLike().execute()
        }

        //タップで投稿の削除
        ivPostMenu.setOnClickListener {

            UserPostDel().execute()

            SweetAlertDialog(this@PostDetails, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("投稿削除完了")
                .setContentText("")
                .setConfirmText("ホーム画面へ")
                .setConfirmClickListener {
                        sDialog -> sDialog.dismissWithAnimation()
                    goHome()
                }
                .show()

        }
    }

    private inner class UserPostGet() : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            var userId = ""
            val db = _helper.writableDatabase
            val sql = "select * from userInfo"
            val cursor = db.rawQuery(sql, null)

            while(cursor.moveToNext()){

                val idxId = cursor.getColumnIndex("user_id")
                userId = cursor.getString(idxId)
            }

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/PostDetailsGet.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("user",userId)
            formBuilder.add("post",postId)
            //リクエストの内容にformを追加
            val form = formBuilder.build()

            //リクエストを生成
            val request = Request.Builder().url(url).post(form).build()

            try {
                val response = client.newCall(request).execute()
                return response.body()!!.string()
            }
            catch (e: IOException) {
                e.printStackTrace()
                return "Error"
            }
        }

        override fun onPostExecute(result: String) {

            try {

                var postData = Gson().fromJson(result, PostDataClassList::class.java)

                Glide.with(this@PostDetails).load(postData.postImage).into(ivPostImage)

                /**
                 * 位置座標から住所に変換するサンプル
                 */
                val geocoder = Geocoder(this@PostDetails)
                //この下の、latitude, longitudeを写真の投稿場所に置き換えればOK
                val addressList: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
                val adminArea = addressList?.first()!!.adminArea

                //tvUserName.text = postData.userName
                //tvUserName.text = adminArea
                tvPostName.text = postData.userName
                tvPostDate.text = postData.postDate
                tvPostText.text = postData.postText

                if(postData.likeFlag){

                    ivPostLike.setImageResource(R.drawable.post_like_true)
                }
            }
            catch(e : Exception){

            }
        }
    }

    private inner class UserPostLike() : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            var id = ""
            val db = _helper.writableDatabase
            val sql = "select * from userInfo"
            val cursor = db.rawQuery(sql, null)

            while(cursor.moveToNext()){

                val idxId = cursor.getColumnIndex("user_id")
                id = cursor.getString(idxId)
            }

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/UserLikeSend.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("user",id)
            formBuilder.add("post",postId)
            //リクエストの内容にformを追加
            val form = formBuilder.build()

            //リクエストを生成
            val request = Request.Builder().url(url).post(form).build()

            try {
                val response = client.newCall(request).execute()
                return response.body()!!.string()
            }
            catch (e: IOException) {
                e.printStackTrace()
                return "Error"
            }
        }

        override fun onPostExecute(result: String) {

            try {

                var likeFlag = Gson().fromJson(result, like::class.java)
                likeToggle(likeFlag.flag)
            }
            catch(e : Exception){

            }
        }
    }

    private inner class UserPostDel() : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {
            var userId = ""
            val db = _helper.writableDatabase
            val sql = "select * from userInfo"
            val cursor = db.rawQuery(sql, null)

            while (cursor.moveToNext()) {

                val idxId = cursor.getColumnIndex("user_id")
                userId = cursor.getString(idxId)
            }

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/UserPostDel.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("user", userId)
            formBuilder.add("post", postId)
            //formBuilder.add("image", imageId)
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

        }
    }

    fun likeToggle(flag : Boolean){

        if(flag) {

            ivPostLike.setImageResource(R.drawable.post_like_true)
            var animation = AnimationUtils.loadAnimation(this,R.anim.like_touch)
            ivPostLike.startAnimation(animation)
        }
        else{

            ivPostLike.setImageResource(R.drawable.post_like_false)
            var animation = AnimationUtils.loadAnimation(this,R.anim.like_touch)
            ivPostLike.startAnimation(animation)
        }
    }

    private inner class UserReplyGet : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/PostReplyGet.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("post", postId)
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

            Log.d("aaaaa",result)
            try {


                var postList = mutableListOf<ReplyList>()
                val listType = object : TypeToken<List<PostDataClassList>>() {}.type
                val postData = Gson().fromJson<List<PostDataClassList>>(result, listType)

                for (i in postData) {

                    postList.add(
                        ReplyList(
                            i.userId,
                            i.userName,
                            i.userIcon,
                            i.postText,
                            i.postDate
                        )
                    )
                }

                lvReplyData.adapter = ReplyAdapter(this@PostDetails, postList)
            }
            catch(e : Exception){

            }
        }
    }


    override fun onDestroy(){

        //ヘルパーオブジェクトの開放
        _helper.close()
        super.onDestroy()
    }

    fun goHome() {
        startActivity(Intent(this, NavigationActivity::class.java))
        finish()
    }
}