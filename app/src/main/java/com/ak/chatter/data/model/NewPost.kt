package com.ak.chatter.data.model

data class NewPost(
    val userId: String = "",
    val postId: String = "",
    val profilePhotoImage: String = "",
    val name: String = "",
    val postImage: String = "",
    val likesNumber: Int = 0,
    val description: String = ""
)