package com.teamforce.thanksapp.data.entities.notifications


import com.google.gson.annotations.SerializedName

data class PushTokenEntity(
    @SerializedName("device")
    val device: String,
    @SerializedName("token")
    val token: String
)