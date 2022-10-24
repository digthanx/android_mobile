package com.teamforce.thanksapp.data.entities.feed


import com.google.gson.annotations.SerializedName

data class Challenge(
    @SerializedName("creat ed_at")
    val createdAt: String,
    @SerializedName("creator_first_name")
    val creatorFirstName: String?,
    @SerializedName("creator_id")
    val creatorId: Int,
    @SerializedName("creator_surname")
    val creatorSurname: String?,
    @SerializedName("creator_tg_name")
    val creatorTgName: String?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("user_liked")
    val userLiked: Boolean
)