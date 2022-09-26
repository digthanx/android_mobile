package com.teamforce.thanksapp.data.request

import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody

data class SendCoinsRequest(
    val recipient: Int,
    val amount: Int,
    val reason: String,
    val is_anonymous: Boolean,
    @SerializedName("tags")
    val tags: RequestBody?
)
