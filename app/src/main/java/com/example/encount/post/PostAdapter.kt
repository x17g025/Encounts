package com.example.encount.post

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.example.encount.PostList
import com.example.encount.PostList2
import com.example.encount.R
import kotlinx.android.synthetic.main.grid_items.view.*

/**
 * やってること
 * カスタムListViewを適用するためのクラス
 *
 * 製作者：中村
 */

class PostAdapter(val context: Context?, val posts: List<PostList>): BaseAdapter() {

    val layoutInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return posts.count()
    }

    override fun getItem(position: Int): PostList {
        return posts[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = layoutInflater.inflate(R.layout.grid_items, parent, false)
       // view.PostUserText.text = posts[position].text
        view.PostId.text       = posts[position].postId
        view.UserId.text       = posts[position].userId
        Glide.with(context).load(posts[position].image).into(view.image_view)

        return view
    }
}

