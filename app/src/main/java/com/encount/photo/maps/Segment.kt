package com.encount.photo.maps

import com.encount.photo.R
import com.google.android.gms.maps.model.LatLng

enum class Segment(val title: String, val flowerName: String, val imageResId: Int, val coordinate: LatLng) {
    Kita("北区", "バラ", R.drawable.app_icon, LatLng(34.705374, 135.510049)),
    Miyakojima("都島区", "サクラ・コスモス", R.drawable.app_icon, LatLng(34.701527, 135.528069)),
    Fukushima("福島区", "のだふじ", R.drawable.app_icon, LatLng(34.692609, 135.472285)),
    Konohana("此花区", "サクラ・チューリップ", R.drawable.app_icon, LatLng(34.683126, 135.452354)),
    Chuo("中央区", "パンジー・ウメ", R.drawable.app_icon, LatLng(34.683405, 135.509931)),
    Nishi("西区", "サクラ・バラ・パンジー・コスモス", R.drawable.app_icon, LatLng(34.676549, 135.486017)),
    Minato("港区", "ヒマワリ・サクラ", R.drawable.app_icon, LatLng(34.664166, 135.460590)),
    Taisho("大正区", "ツツジ", R.drawable.app_icon, LatLng(34.650682, 135.472726)),
    Tennoji("天王寺区", "モモ・パンジー", R.drawable.app_icon, LatLng(34.658161, 135.519413)),
    Naniwa("浪速区", "ナデシコ", R.drawable.app_icon, LatLng(34.659672, 135.499627)),
    NishiYodogawa("西淀川区", "サザンカ", R.drawable.app_icon, LatLng(34.711690, 135.456205)),
    Yodogawa("淀川区", "パンジー", R.drawable.app_icon, LatLng(34.721358, 135.486542)),
    HigashiYodogawa("東淀川区", "コブシ", R.drawable.app_icon, LatLng(34.741211, 135.529455)),
    Higashinari("東成区", "バラ・パンジー", R.drawable.app_icon, LatLng(34.670089, 135.541221)),
    Ikuno("生野区", "アジサイ", R.drawable.app_icon, LatLng(34.653839, 135.534360)),
    Asahi("旭区", "ハナショウブ", R.drawable.app_icon, LatLng(34.721273, 135.544143)),
    Joto("城東区", "モクレン・コスモス", R.drawable.app_icon, LatLng(34.703228, 135.544773)),
    Tsurumi("鶴見区", "ハナミヅキ・ツバキ・チューリップ・ニチニチソウ", R.drawable.app_icon, LatLng(34.704482, 135.574270)),
    Abeno("阿倍野区", "モモ・ベチュニア", R.drawable.app_icon, LatLng(34.638713, 135.518504)),
    Suminoe("住之江区", "サザンカ", R.drawable.app_icon, LatLng(34.609638, 135.482819)),
    Sumiyoshi("住吉区", "カキツバタ", R.drawable.app_icon, LatLng(34.603773, 135.500512)),
    HigashiSumiyoshi("東住吉区", "ナデシコ", R.drawable.app_icon, LatLng(34.622084, 135.526689)),
    Hirano("平野区", "ワタ", R.drawable.app_icon, LatLng(34.621238, 135.545991)),
    Nishinari("西成区", "ハギ", R.drawable.app_icon, LatLng(34.635097, 135.494589))
}