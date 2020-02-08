package com.encount.photo.maps

import com.encount.photo.R
import com.google.android.gms.maps.model.LatLng

enum class Segment(val title: String, val flowerName: String, val imageResId: Int, val coordinate: LatLng) {
    Kita("北区", "バラ", R.drawable.app_icon, LatLng(35.7707407, 140.0022931)),
    Miyakojima("都島区", "サクラ・コスモス", R.drawable.app_icon, LatLng(35.701527, 140.0022731)),
    Fukushima("福島区", "のだふじ", R.drawable.app_icon, LatLng(35.692609, 140.0022831))
}