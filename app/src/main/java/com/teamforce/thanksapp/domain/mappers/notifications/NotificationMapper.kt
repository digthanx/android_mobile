package com.teamforce.thanksapp.domain.mappers.notifications

import androidx.compose.runtime.internal.updateLiveLiteralValue
import com.teamforce.thanksapp.data.entities.NotificationEntity
import com.teamforce.thanksapp.domain.models.notifications.NotificationModel
import com.teamforce.thanksapp.domain.models.notifications.NotificationType
import javax.inject.Inject

class NotificationMapper @Inject constructor() {
    fun map(from: NotificationEntity): NotificationModel {
        return NotificationModel(
            type = mapType(from.type),
            objectId = from.objectId ?: -1,
            theme = from.theme ?: "Unknown",
            text = from.text ?: "Unknown",
            isRead = from.isRead,
            createdAt = from.createdAt,
            updatedAt = from.updatedAt
        )
    }

    private fun mapType(stringType: String): NotificationType {
        return when (stringType) {
            "T", "t" -> NotificationType.transaction
            "H", "h" -> NotificationType.challenge
            "C", "c" -> NotificationType.comment
            "L", "l" -> NotificationType.like
            else -> NotificationType.unknown
        }
    }
}