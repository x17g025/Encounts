package com.encount.photo.maps

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SpotAdapter(fm: FragmentManager, private val context: Context): FragmentPagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(i: Int): Fragment {

        when (i) {
            0    -> return SpotPopPost()
            else -> return SpotNewPost()
        }

    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0    -> return "人気"
            else -> return "新着"
        }
    }
}