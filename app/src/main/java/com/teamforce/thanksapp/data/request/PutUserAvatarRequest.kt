package com.teamforce.thanksapp.data.request

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody


data class PutUserAvatarRequest(
    @SerializedName("photo")
    val photo: MultipartBody.Part
)