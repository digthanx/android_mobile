package com.teamforce.thanksapp.data.response

import com.google.gson.annotations.SerializedName

data class VerificationResponse(
    var type: String = "",
    @SerializedName("is_success") var isSuccess: Boolean = false,
    var token: String = ""
)
