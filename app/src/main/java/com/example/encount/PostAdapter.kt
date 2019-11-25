package com.example.encount

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.post_list.view.*

/**
 * やってること
 * カスタムListViewを適用するためのクラス
 *
 * 製作者：中村
 */

class PostAdapter(val context: Context, val posts: List<post>): BaseAdapter() {

    val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return posts.count()
    }

    override fun getItem(position: Int): post {
        return posts[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = layoutInflater.inflate(R.layout.post_list, parent, false)
        view.PostUserName.text = posts[position].name
        view.PostUserText.text = posts[position].text
        view.PostDate.text     = posts[position].date
        view.PostId.text       = posts[position].postid
        view.UserId.text       = posts[position].userid
        Glide.with(context).load(posts[position].image).into(view.PostImage)

        return view
    }
}

