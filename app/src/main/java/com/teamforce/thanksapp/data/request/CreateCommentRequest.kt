package com.teamforce.thanksapp.data.request

data class CreateCommentRequest(
    val transaction: Int,
    val text: String
    //val picture: String?
)
