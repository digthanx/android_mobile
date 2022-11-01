package com.teamforce.thanksapp.domain.models.notifications

import java.util.*

sealed class NotificationItem {
    data class NotificationModel(
        val type: NotificationType,
        val objectId: Int,
        val theme: String,
        val text: String,
        val isRead: Boolean,
        val createdAt: String,
        val updatedAt: String,
        val id: Int
    ) : NotificationItem()

    data class DateTimeSeparator(
        val date: String,
        val uuid: UUID = UUID.randomUUID()
    ) : NotificationItem()
}


enum class NotificationType {
    Transaction,
    Challenge,
    Comment,
    Like,
    Unknown
}