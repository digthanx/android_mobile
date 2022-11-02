package com.teamforce.thanksapp.domain.mappers.notifications

import com.teamforce.thanksapp.data.entities.NotificationEntity
import com.teamforce.thanksapp.domain.models.notifications.NotificationItem
import com.teamforce.thanksapp.domain.models.notifications.NotificationType
import javax.inject.Inject

class NotificationMapper @Inject constructor() {
    fun map(from: NotificationEntity): NotificationItem.NotificationModel {
        return NotificationItem.NotificationModel(
            type = mapType(from.type),
            objectId = from.objectId ?: -1,
            theme = from.theme ?: "Unknown",
            text = from.text ?: "Unknown",
            isRead = from.isRead,
            createdAt = from.createdAt,
            updatedAt = from.updatedAt,
            id = from.id
        )

    }

    private fun mapType(stringType: String): NotificationType {
        return when (stringType.lowercase()) {
            "t" -> NotificationType.Transaction
            "h" -> NotificationType.Challenge
            "c" -> NotificationType.Comment
            "l" -> NotificationType.Like
            else -> NotificationType.Unknown
        }
    }
}