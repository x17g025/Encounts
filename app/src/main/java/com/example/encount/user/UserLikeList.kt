package com.example.encount.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.encount.R

class UserLikeList : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.activity_user_like_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


    }
}
