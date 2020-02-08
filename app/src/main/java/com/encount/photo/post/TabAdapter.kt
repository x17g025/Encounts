package com.encount.photo.post

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class TabAdapter(fm: FragmentManager, private val context: Context, id : String): FragmentPagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    var userId = id

    override fun getItem(i: Int): Fragment {

        when (i) {
            0    -> return UserPostList(userId)
            else -> return UserLikeList(userId)
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0    -> return "投稿"
            else -> return "いいね"
        }
    }
}