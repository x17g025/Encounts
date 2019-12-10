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
    val likeid: String,
    val postid: String,
    val userid: String,
    val name: String,
    val text: String,
    val date: String,
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