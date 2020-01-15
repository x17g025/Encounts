package com.example.encount2.user

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class TabAdapter(fm: FragmentManager, private val context: Context): FragmentPagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(i: Int): Fragment {

        when (i) {
            0 -> return UserPostList()
            1 -> return UserFriendList()
            else -> return UserLikeList()
        }

    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return "投稿"
            1 -> return "フレンド"
            else -> return "いいね"
        }
    }

}