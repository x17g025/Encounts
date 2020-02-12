package com.encount.photo.post

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.encount.photo.R
import com.encount.photo.ReplyList
import kotlinx.android.synthetic.main.la_reply_item.view.*
import kotlinx.android.synthetic.main.la_reply_item.view.tvUserId

/**
 * やってること
 * カスタムListViewを適用するためのクラス
 *
 * 製作者：中村
 */

class ReplyAdapter(val context: Context?, val posts: List<ReplyList>): BaseAdapter() {

    val layoutInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return posts.count()
    }

    override fun getItem(position: Int): ReplyList {
        return posts[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = layoutInflater.inflate(R.layout.la_reply_item, parent, false)
        view.tvReplyName.text = posts[position].userName
        view.tvReplyText.text = posts[position].postText
        view.tvUserId.text    = posts[position].userId
        view.tvReplyDate.text = posts[position].postDate

        return view
    }
}

