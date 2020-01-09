package com.example.encount.user

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class UserProfileAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {

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