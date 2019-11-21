package com.example.encount

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_user_settings.*
import kotlinx.android.synthetic.main.post_list.view.*

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
        Glide.with(context).load(posts[position].image).into(view.PostImage)
        view.PostLike.setOnClickListener {

            PostLikeImage.
        }
        return view
    }
}

class post(
    val name: String,
    val text: String,
    val image: String
)