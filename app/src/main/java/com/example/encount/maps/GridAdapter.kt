package com.example.encount.maps

import android.content.Context
import android.graphics.Point
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.BaseAdapter
import android.widget.ImageView
import com.example.encount.R
import com.squareup.picasso.Picasso
import java.util.ArrayList
import java.util.Collections
import java.util.Locale
import android.content.Context.WINDOW_SERVICE
import com.bumptech.glide.Glide
import com.example.encount.PostList
import com.example.encount.PostList2
import kotlinx.android.synthetic.main.grid_items.view.*

/**
 * やってること
 * カスタムGridViewを適用するためのクラス
 *
 * 製作者：大野
 */

class GridAdapter(val context: Context?, val posts: List<PostList2>): BaseAdapter() {

    val layoutInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

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

        val view = layoutInflater.inflate(R.layout.grid_items, parent, false)
        view.tvUserId.text       = posts[position].userId
        view.tvImageId.text      = posts[position].imageId
        Glide.with(context).load(posts[position].imagePath).into(view.image_view)

        return view
    }
}


/*
class GridAdapter internal constructor(
    private val context: Context,
    private val layoutId: Int,
    iList: Array<String>
) : BaseAdapter() {
    private val inflater: LayoutInflater
    private val imageList = ArrayList<String>()

    private var ScreenWHalf = 0

    init {
        this.inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        Collections.addAll(imageList, *iList)

        // 画面の横幅の半分を計算
        val wm = context.getSystemService(WINDOW_SERVICE) as WindowManager
        if (wm != null) {
            val disp = wm.defaultDisplay
            val size = Point()
            disp.getSize(size)

            val screenWidth = size.x
            ScreenWHalf = screenWidth / 2
            Log.d("debug", "ScreenWidthHalf=$ScreenWHalf")
        }

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View

        if (convertView == null) {
            view = inflater.inflate(layoutId, parent, false)
        } else {
            view = convertView
        }

        val img = view.findViewById<ImageView>(R.id.image_view)
        img.setScaleType(ImageView.ScaleType.CENTER_CROP)

        /*Picasso.with(context)
            .load(addUrl(position))
            .resize(ScreenWHalf, ScreenWHalf)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error)
            .into(img)*/

        return view
    }

    // ネットワークアクセスするURLを設定する
    private fun addUrl(number: Int): String {

        return String.format(
            Locale.US,
            "https://kinako.cf/files/postImg/%s.jpg", // 自分のサーバーに上げて見ましょう
            imageList[number]
        )
    }

    override fun getCount(): Int {
        // 全要素数を返す
        return imageList.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }
}*/