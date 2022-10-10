package com.teamforce.thanksapp.data.response

data class GetChallengeResultResponse(
    val updated_at: String,
    val text: String?,
    val photo: String?,
    val status: String,
    val received: Int
)
