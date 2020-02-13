package com.encount.photo.post

import android.content.Intent
import android.location.Geocoder
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.encount.photo.*
import com.encount.photo.user.UserProfile
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_post_details.*
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
    var userId = ""
    var preId = ""
    var text   = ""
    var preAct = ""
    var _id = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        _id = doSelectSQLite(this)

        postId = intent.getStringExtra("Post_Id")!!
        preId  = intent.getStringExtra("User_Id")!!
        preAct = intent.getStringExtra("Pre_Act")!!

        UserPostGet().execute()

        ivPostLike.setOnClickListener {

            UserPostLike().execute()
        }

        ivPostReply.setOnClickListener{

            startActivity(Intent(this, PostReplyList::class.java)
                .putExtra("Post_Id", postId)
                .putExtra("Pre_Act", preAct)
                .putExtra("User_Id", preId))
        }

        llUserData.setOnClickListener{

            if(preAct != "spot" && preAct != "map" || userId == _id) {

                startActivity(Intent(this, UserProfile::class.java)
                    .putExtra("User_Id", userId))
            }
        }

        //タップで投稿の削除
        ivPostMenu.setOnClickListener {

            if (userId == _id) {
                SweetAlertDialog(this@PostDetails, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("投稿を削除します")
                    .setContentText("削除した投稿は元に戻せません。")
                    .setConfirmText("Yes")
                    .setConfirmClickListener { sDialog -> sDialog.dismissWithAnimation()
                        //削除処理
                        UserPostDel().execute()
                        SweetAlertDialog(this@PostDetails, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("投稿削除完了")
                            .setContentText("")
                            .setConfirmText("OK")
                            .setConfirmClickListener {
                                    sDialog -> sDialog.dismissWithAnimation()
                                goHome()
                            }
                            .show()
                    }
                    .setCancelButton(
                        "No"
                    ) { sDialog -> sDialog.dismissWithAnimation() }
                    .show()
            } else {
                SweetAlertDialog(this@PostDetails, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error!")
                    .setContentText("他の人の投稿は消せません")
                    .setConfirmText("OK")
                    .setConfirmClickListener { sDialog ->
                        sDialog.dismissWithAnimation()
                    }
                    .show()
            }
        }
    }

    private inner class UserPostGet : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/PostDetailsGet.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("user", _id)
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

            try {

                var postData = Gson().fromJson(result, PostDataClassList::class.java)

                Glide.with(this@PostDetails).load(postData.postImage).into(ivPostImage)
                Glide.with(this@PostDetails).load(postData.userIcon).into(ivUserIcon)

                userId           = postData.userId
                tvPostName.text  = postData.userName
                tvPostDate.text  = postData.postDate

                if (postData.likeFlag) {

                    ivPostLike.setImageResource(R.drawable.tool_like_true)
                }

                if(postData.postText.isEmpty()){

                    llPostText.visibility = View.GONE
                }
                else{

                    tvPostText.text  = postData.postText
                }
            } catch (e: Exception) {

            }
        }
    }

    private inner class UserPostLike : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/UserLikeSend.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("user", _id)
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

            try {

                var likeFlag = Gson().fromJson(result, flag::class.java)
                likeToggle(likeFlag.flag)
            }
            catch (e: Exception) {

            }
        }
    }

    private inner class UserPostDel() : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/UserPostDel.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("user", _id)
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

    fun likeToggle(flag: Boolean) {

        if (flag) {

            ivPostLike.setImageResource(R.drawable.tool_like_true)
            var animation = AnimationUtils.loadAnimation(this, R.anim.like_touch)
            ivPostLike.startAnimation(animation)
        } else {

            ivPostLike.setImageResource(R.drawable.tool_like_false)
            var animation = AnimationUtils.loadAnimation(this, R.anim.like_touch)
            ivPostLike.startAnimation(animation)
        }
    }

    //位置情報を住所に変換する関数
    private fun getAddress(lat: Double, lng: Double): String {
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocation(lat, lng, 1)
        //println("0" + list[0].getAddressLine(0))
        var kekka = list[0].getAdminArea() + list[0].getLocality() + list[0].getThoroughfare() +list[0].getSubThoroughfare()
        return kekka
    }

    fun goHome() {
        startActivity(Intent(this, NavigationActivity::class.java))
        finish()
    }
}