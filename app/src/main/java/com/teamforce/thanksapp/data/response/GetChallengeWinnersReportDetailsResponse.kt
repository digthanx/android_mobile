package com.teamforce.thanksapp.data.response

import com.google.gson.annotations.SerializedName

data class GetChallengeWinnersReportDetailsResponse(
    val challenge: Challenge,
    val user: User,
    @SerializedName("text")
    val challengeText: String,
    @SerializedName("photo")
    val challengePhoto: String
){
    data class Challenge(
        val id: Int,
        val name: String
    )

    data class User(
        val id: Int,
        val tg_name: String,
        val name: String,
        val surname: String,
        val avatar: String
    )
}
