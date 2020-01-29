package com.example.encount.post

import android.content.Intent
import android.location.Geocoder
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.encount.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    var text   = ""
    var imageLat   = ""
    var imageLng   = ""
  
    private val _helper = SQLiteHelper(this@PostDetails)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        postId = intent.getStringExtra("Post_Id")

        if(intent.getStringExtra("User_Id") != null) {

            userId = intent.getStringExtra("User_Id") //投稿者のユーザーID
        }

        UserPostGet().execute()
        UserReplyGet().execute()


        //タップで投稿の詳細画面へ
        ivPostLike.setOnClickListener {

            UserPostLike().execute()
        }

        //タップで投稿の削除
        ivPostMenu.setOnClickListener {
            var id = ""
            val db = _helper.writableDatabase
            val sql = "select * from userInfo"
            val cursor = db.rawQuery(sql, null)

            while (cursor.moveToNext()) {

                val idxId = cursor.getColumnIndex("user_id")
                id = cursor.getString(idxId)
            }

            if (userId == id) {
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

        ivPostReply.setOnClickListener {

            var id = ""
            val db = _helper.writableDatabase
            val sql = "select * from userInfo"
            val cursor = db.rawQuery(sql, null)

            while (cursor.moveToNext()) {

                val idxId = cursor.getColumnIndex("user_id")
                id = cursor.getString(idxId)
            }

            if (userId == id) {
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

        btnReply.setOnClickListener {

            text = etReply.text.toString()
            //txInfo.text = ""

            if(text != ""){

                UserReplySend().execute()
            }
            else{

                //txInfo.text = "ユーザーまたはパスワードが入力されていません"
            }
        }
    }

    private inner class UserPostGet() : AsyncTask<String, String, String>() {

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
            val url = "https://encount.cf/encount/PostDetailsGet.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("user", userId)
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

                Log.d("loac",postData.imageLat.toString())
                tvPostName.text  = postData.userName
                tvPostDate.text  = postData.postDate
                tvPostText.text  = postData.postText
                tvPostPlace.text = getAddress(postData.imageLat, postData.imageLng)

                if (postData.likeFlag) {

                    ivPostLike.setImageResource(R.drawable.tool_like_true)
                }
            } catch (e: Exception) {

            }
        }
    }

    private inner class UserPostLike() : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            var id = ""
            val db = _helper.writableDatabase
            val sql = "select * from userInfo"
            val cursor = db.rawQuery(sql, null)

            while (cursor.moveToNext()) {

                val idxId = cursor.getColumnIndex("user_id")
                id = cursor.getString(idxId)
            }

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/UserLikeSend.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("user", id)
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

                var likeFlag = Gson().fromJson(result, like::class.java)
                likeToggle(likeFlag.flag)
            } catch (e: Exception) {

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
            Log.d("debug","u"+userId + "p" + postId)
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

            try {

                var postList = mutableListOf<ReplyList>()
                val listType = object : TypeToken<List<PostDataClassList>>() {}.type
                val postData = Gson().fromJson<List<PostDataClassList>>(result, listType)

                for (i in postData) {

                    postList.add(
                        ReplyList(
                            i.userId,
                            i.userName,
                            i.postText,
                            i.postDate
                        )
                    )
                }

                lvReplyData.adapter = ReplyAdapter(this@PostDetails, postList)
            } catch (e: Exception) {

            }
        }
    }

    private inner class UserReplySend() : AsyncTask<String, String, String>() {

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
            val url = "https://encount.cf/encount/PostReplySend.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("user",userId)
            formBuilder.add("post",postId)
            formBuilder.add("text",text)
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

            etReply.getEditableText().clear()

            SweetAlertDialog(this@PostDetails, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("投稿完了")
                .setContentText("")
                .setConfirmText("OK")
                .setConfirmClickListener {
                    sDialog -> sDialog.dismissWithAnimation()
                }
                .show()

           UserReplyGet().execute()
        }
    }

    override fun onDestroy() {

        //ヘルパーオブジェクトの開放
        _helper.close()
        super.onDestroy()
    }

    fun goHome() {
        startActivity(Intent(this, NavigationActivity::class.java))
        finish()
    }
}