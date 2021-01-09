package com.ak.chatter.data.model

data class User(
    val uid: String = "",
    var idUserDocument: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val birthday: String = "",
    val gender: String = "",
    val email: String = ""
)
