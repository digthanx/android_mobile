package com.teamforce.thanksapp.data.entities.notifications

import com.google.gson.annotations.SerializedName

data class UnreadNotificationsAmountEntity(
    @SerializedName("unread_notifications")
    val unreadNotificationsAmount: Int
)
