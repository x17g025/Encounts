package com.encount.photo.user

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.encount.photo.*
import com.encount.photo.FriendList
import com.google.gson.Gson
import kotlinx.android.synthetic.main.la_friend_item.view.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception


/**
 * やってること
 * カスタムListViewを適用するためのクラス
 *
 * 製作者：中村
 */

class RequestAdapter(val context: Context?, val posts: List<FriendList>): BaseAdapter() {

    val layoutInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var _id = doSelectSQLite(context)
    var userId = ""
    var viewId : View? = null

    override fun getCount(): Int {
        return posts.count()
    }

    override fun getItem(position: Int): FriendList {
        return posts[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = layoutInflater.inflate(R.layout.la_friend_item, parent, false)
        view.tvFriendName.text = posts[position].userName
        Glide.with(context).load(posts[position].userIcon).into(view.ivUserIcon)

        view.setOnClickListener {

            val intent = Intent(context, UserProfile::class.java)
            intent.putExtra("User_Id", posts[position].userId)
            view.getContext().startActivity(intent)
        }

        view.ivFriendAdd.setOnClickListener{

            userId = posts[position].userId
            viewId = view

            FriendAdd().execute()
        }

        return view
    }

    private inner class FriendAdd : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/FriendAdd.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("id",_id)
            formBuilder.add("other",userId)
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

            try{
                val userData = Gson().fromJson(result, UserDataClassList::class.java)

                when (userData.followFlag) {

                    1 -> {

                        viewId!!.ivFriendAdd.setImageResource(R.drawable.tool_check)
                    }
                    else -> {

                        viewId!!.ivFriendAdd.setImageResource(R.drawable.tool_wait)
                    }
                }
            }
            catch(e : Exception){
            }
        }
    }
}

