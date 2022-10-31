package com.teamforce.thanksapp.data.request

import com.google.gson.annotations.SerializedName

data class GetCommentsRequest(
    @SerializedName("transaction_id")
    val transaction_id: Int,
    @SerializedName("include_name")
    val include_name: Boolean = true,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("offset")
    val offset: Int
)