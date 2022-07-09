package com.teamforce.thanksapp.data.request

class SendCoinsRequest(
    val recipient: Int,
    val amount: Int,
    val reason: String
)
