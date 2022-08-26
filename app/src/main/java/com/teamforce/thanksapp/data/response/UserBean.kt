package com.teamforce.thanksapp.data.response

import com.google.gson.annotations.SerializedName

data class UserBean(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("tg_name") val tgName: String,
    @SerializedName("name") val firstname: String,
    @SerializedName("surname") val surname: String,
    @SerializedName("photo") val photo: String?
)
