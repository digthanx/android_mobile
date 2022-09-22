package com.teamforce.thanksapp.model.domain

data class CommentModel(
    val id: Int,
    val text: String,
    val picture: String?,
    val created: String,
    val edited: String,
    val user: User,

){
    data class User(
        val id: Int,
        val name: String,
        val avatar: String?
    )
}
