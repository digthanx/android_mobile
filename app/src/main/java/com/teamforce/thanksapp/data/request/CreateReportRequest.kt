package com.teamforce.thanksapp.data.request

import com.google.gson.annotations.SerializedName

data class CreateReportRequest(
    @SerializedName("challenge")
    val challengeId: Int,
    val text: String,
    val photo: String?
)
