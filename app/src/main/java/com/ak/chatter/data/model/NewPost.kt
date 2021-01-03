package com.ak.chatter.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class NewPost(
    val userId: String = "",
    val postId: String = "",
    val profilePhotoImage: String = "",
    val name: String = "",
    val postImage: String = "",
    val likesNumber: Int = 0,
    val description: String = "",
    @ServerTimestamp
    val date: Date = Date()
)