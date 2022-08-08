package com.teamforce.thanksapp.data.response

import com.google.gson.annotations.SerializedName

class ProfileResponse(
    val username: String,
    val profile: ProfileBean,
    val detail: String?
)

class ProfileBean(
    @SerializedName("id")
    val id: String,
    @SerializedName("organization")
    val organization: String,
    @SerializedName("department")
    val department: String,
    @SerializedName("tg_id") val tgId: String,
    @SerializedName("tg_name") val tgName: String,
    val photo: String,
    @SerializedName("hired_at") val hiredAt: String,
    val surname: String,
    @SerializedName("first_name") val firstname: String,
    @SerializedName("middle_name") val middlename: String,
    @SerializedName("nickname")
    val nickname: String
)
