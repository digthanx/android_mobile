package com.teamforce.thanksapp.data.request

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    @SerializedName("tg_name") val  tgName: String?,
    @SerializedName("surname") val  surname: String?,
    @SerializedName("first_name") val  firstName: String?,
    @SerializedName("middle_name") val middleName: String?,
    @SerializedName("nickname") val nickname: String?
)
