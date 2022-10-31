package com.teamforce.thanksapp.data.entities.feed


import com.google.gson.annotations.SerializedName

data class Winner(
    @SerializedName("challenge_id")
    val challengeId: Int,
    @SerializedName("challenge_name")
    val challengeName: String?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("user_liked")
    val userLiked: Boolean,
    @SerializedName("winner_first_name")
    val winnerFirstName: String?,
    @SerializedName("winner_id")
    val winnerId: Int,
    @SerializedName("winner_photo")
    val winnerPhoto: String,
    @SerializedName("winner_surname")
    val winnerSurname: String?,
    @SerializedName("winner_tg_name")
    val winnerTgName: String?
)