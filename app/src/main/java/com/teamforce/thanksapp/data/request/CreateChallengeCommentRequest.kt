package com.teamforce.thanksapp.data.request

data class CreateChallengeCommentRequest(
    val challenge_id: Int,
    val text: String
)
