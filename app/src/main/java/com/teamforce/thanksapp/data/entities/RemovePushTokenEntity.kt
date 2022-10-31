package com.teamforce.thanksapp.data.entities

import com.google.gson.annotations.SerializedName

data class RemovePushTokenEntity(
    @SerializedName("device")
    val device: String,
    @SerializedName("user_id")
    val userId: String
)