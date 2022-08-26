package com.teamforce.thanksapp.data.request

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


data class PutUserAvatarRequest(
    @SerializedName("photo")
    val photo: RequestBody
)