package com.encount.photo.maps

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.BaseAdapter
import com.encount.photo.R
import com.bumptech.glide.Glide
import com.encount.photo.PostList2
import com.encount.photo.SQLiteHelper
import com.encount.photo.like
import com.encount.photo.post.PostDetails
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ga_post_item.view.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception

/**
 * やってること
 * スポット詳細画面へ
 * カスタムGridViewを適用するためのクラス
 *
 * 製作者：大野
 */

class GridAdapter(val context: Context?, val posts: List<PostList2>): BaseAdapter() {

    val layoutInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val _helper = SQLiteHelper(context)
    var postId = "a"
    var viewId : View? = null

    override fun getCount(): Int {
        return posts.count()
    }

    override fun getItem(position: Int): PostList2 {
        return posts[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = layoutInflater.inflate(R.layout.ga_post_item, parent, false)
        view.tvUserId.text       = posts[position].userId
        view.tvImageId.text      = posts[position].imageId
        view.tvImageLat.text       = posts[position].imageLat
        view.tvImageLng.text      = posts[position].imageLng
        Glide.with(context).load(posts[position].imagePath).into(view.image_view)

        if(posts[position].likeFlag){

            view.ivPostLike.setImageResource(R.drawable.tool_like_true)
        }
        else{

            view.ivPostLike.setImageResource(R.drawable.tool_like_false)
        }

        view.image_view.setOnClickListener {
            val intent = Intent(context, PostDetails::class.java)
            intent.putExtra("Post_Id", posts[position].postId)
            intent.putExtra("User_Id", posts[position].userId)
            intent.putExtra("imageLat", posts[position].imageLat)
            intent.putExtra("imageLng", posts[position].imageLng)
            view.getContext().startActivity(intent)
        }

        view.ivPostLike.setOnClickListener{

            postId = posts[position].postId
            viewId = view
            Log.d("baba", postId)
            UserPostLike().execute()
        }

        Handler().postDelayed({

            view.Progress.visibility = View.GONE
            view.ImageNothing.visibility = View.VISIBLE
        }, 15000)

        return view
    }

    private inner class UserPostLike : AsyncTask<String, String, String>() {

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

                if(likeFlag.flag) {

                    viewId!!.ivPostLike.setImageResource(R.drawable.tool_like_true)
                    var animation = AnimationUtils.loadAnimation(context,R.anim.like_touch)
                    viewId!!.ivPostLike.startAnimation(animation)
                }
                else{

                    viewId!!.ivPostLike.setImageResource(R.drawable.tool_like_false)
                    var animation = AnimationUtils.loadAnimation(context,R.anim.like_touch)
                    viewId!!.ivPostLike.startAnimation(animation)
                }

                _helper.close()
            }
            catch(e : Exception){

                _helper.close()
            }
        }
    }
}