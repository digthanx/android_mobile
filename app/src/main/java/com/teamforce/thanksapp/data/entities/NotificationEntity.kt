package com.teamforce.thanksapp.data.entities

import com.google.gson.annotations.SerializedName

data class NotificationEntity(
    @SerializedName("type")
    val type: String,
    @SerializedName("object_id")
    val objectId: Int?,
    @SerializedName("theme")
    val theme: String?,
    @SerializedName("text")
    val text: String?,
    @SerializedName("read")
    val isRead: Boolean,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("id")
    val id: Int
)