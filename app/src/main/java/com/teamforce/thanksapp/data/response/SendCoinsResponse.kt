package com.teamforce.thanksapp.data.response

data class SendCoinsResponse(
    val recipient: Int,
    val amount: Int,
    val reason: String
)
