package com.encount.photo

data class flag(
    val flag: Boolean
)

data class PostList(
    val postId: String,
    val userId: String,
    val likeFlag: Boolean,
    val image: String,
    val preAct : String
)

data class ReplyList(
    val userId: String,
    val userName: String,
    val postText: String,
    val postDate: String
)

data class PostDataClassList(
    val postId: String,
    val userId: String,
    val userName: String,
    val userIcon: String,
    val postImage: String,
    val postText: String,
    val postDate: String,
    val likeFlag: Boolean,
    val postLikeCnt: Long,
    val imageLat: Double,
    val imageLng: Double
)

data class LoginDataClassList(
    val flag: Boolean,
    val result: String,
    val userId: Long
)

data class UserDataClassList(
    val userName: String,
    val userNumber: Long,
    val userBio: String,
    val userIcon: String,
    val postCount: String,
    val likeCount: String,
    val followFlag: Int
)

data class SinginDataClassList (
    val flag: Boolean,
    val result: String
)

data class MapPostData(
    val imageId: String,
    val userId: String,
    val imagePath: String,
    val imageLat: String,
    val imageLng: String,
    val postId: String,
    val likeFlag: Boolean
)