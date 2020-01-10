package com.example.encount

data class like(
    val flag: Boolean
)

data class MapsList(
    val imgpath: String,
    val imglat: Double,
    val imglng: Double
)

data class MapsDataClassList (
    val imgPath: String,
    val imgLat: Double,
    val imgLng: Double
)

data class PostList(
    val postId: String,
    val userId: String,
    val text: String,
    val image: String
)

data class PostDataClassList(
    val likeId: String,
    val postId: String,
    val userId: String,
    val userName: String,
    val userIcon: String,
    val postImage: String,
    val postText: String,
    val postDate: String,
    val postLikeCnt: Long
)

data class LoginDataClassList(
    val flag: Boolean,
    val result: String,
    val userId: Long
)

data class UserDataClassList(
    val userName: String,
    val userNumber: Long,
    val userBio: String
)

data class SinginDataClassList (
    val flag: Boolean,
    val result: String
)

data class FriendDataClassList (
    val mapsLat: Double,
    val mapsLng: Double,
    val imagePath: String
)

data class PostList2(
    val imageId: String,
    val userId: String,
    val imagePath: String,
    val imageLat: String,
    val imageLng: String
)