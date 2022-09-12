package com.teamforce.thanksapp.data.request

data class SendCoinsRequest(
    val recipient: Int,
    val amount: Int,
    val reason: String,
    val is_anonymous: Boolean,
    val tags_list: MutableList<Int>?
)
