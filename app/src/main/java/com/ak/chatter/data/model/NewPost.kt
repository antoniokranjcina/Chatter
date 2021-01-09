package com.ak.chatter.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class NewPost(
    val user: User = User(),
    var idPostDocument: String = "",
    val profilePhotoImage: String = "",
    val postImage: String = "",
    val likesNumber: Int = 0,
    val description: String = "",
    @ServerTimestamp
    val date: Date = Date()
)