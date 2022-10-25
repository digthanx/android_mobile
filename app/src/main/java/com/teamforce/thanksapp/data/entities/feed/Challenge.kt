package com.teamforce.thanksapp.data.entities.feed


import com.google.gson.annotations.SerializedName

data class Challenge(
    @SerializedName("id")
    val id: Int,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("name")
    val name: String?,
    @SerializedName("creator_id")
    val creatorId: Int,
    @SerializedName("user_liked")
    val userLiked: Boolean,
    @SerializedName("creator_first_name")
    val creatorFirstName: String?,
    @SerializedName("creator_surname")
    val creatorSurname: String?,
    @SerializedName("creator_tg_name")
    val creatorTgName: String?,
)