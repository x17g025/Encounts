package com.example.encount.post

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.example.encount.PostList
import com.example.encount.R
import kotlinx.android.synthetic.main.la_bbs_item.view.*

/**
 * やってること
 * カスタムListViewを適用するためのクラス
 *
 * 製作者：中村
 */

class ReplyAdapter(val context: Context?, val posts: List<PostList>): BaseAdapter() {

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

        val view = layoutInflater.inflate(R.layout.la_bbs_item, parent, false)
        view.tvReplyName.text  = posts[position].postId
        view.tvReplyText.text  = posts[position].postId + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
        view.tvUserId.text     = posts[position].userId
        //view.tvPostDate.text   = posts[position].postDate
        Glide.with(context).load(posts[position].image).into(view.ivUserIcon)

        return view
    }
}

