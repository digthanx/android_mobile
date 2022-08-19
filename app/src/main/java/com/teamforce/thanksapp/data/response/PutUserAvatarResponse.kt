package com.teamforce.thanksapp.data.response

import com.google.gson.annotations.SerializedName

data class PutUserAvatarResponse(
    @SerializedName("photo")
    val data: String
)