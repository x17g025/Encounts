package com.example.encount.post

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.example.encount.PostDataClassList
import com.example.encount.PostList
import com.example.encount.R
import com.example.encount.ReplyList
import kotlinx.android.synthetic.main.grid_items.view.*
import kotlinx.android.synthetic.main.la_bbs_item.view.*
import kotlinx.android.synthetic.main.la_bbs_item.view.tvUserId

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

        val view = layoutInflater.inflate(R.layout.la_bbs_item, parent, false)
        view.tvReplyName.text = posts[position].userName
        view.tvReplyText.text = posts[position].postText
        view.tvUserId.text    = posts[position].userId
        view.tvReplyDate.text = posts[position].postDate

        view.setOnClickListener {

            val intent = Intent(context, PostDetails::class.java)
            intent.putExtra("User_Id", posts[position].userId)
            view.getContext().startActivity(intent)
        }

        return view
    }
}

