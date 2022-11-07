package com.teamforce.thanksapp.domain.mappers.notifications

import com.teamforce.thanksapp.data.entities.notifications.NotificationChallengeDataEntity
import com.teamforce.thanksapp.data.entities.notifications.NotificationEntity
import com.teamforce.thanksapp.data.entities.notifications.NotificationTransactionDataEntity
import com.teamforce.thanksapp.domain.models.notifications.NotificationAdditionalData
import com.teamforce.thanksapp.domain.models.notifications.NotificationItem
import com.teamforce.thanksapp.domain.models.notifications.NotificationType
import javax.inject.Inject

class NotificationMapper @Inject constructor() {
    fun map(from: NotificationEntity): NotificationItem.NotificationModel {
        return NotificationItem.NotificationModel(
            theme = from.theme ?: "Unknown",
            isRead = from.isRead,
            createdAt = from.createdAt,
            updatedAt = from.updatedAt,
            id = from.id,
            data = when (mapType(from.type)) {
                NotificationType.Transaction -> mapNotificationTransactionData(from.transactionData!!)
                NotificationType.Challenge -> mapNotificationChallengeData(from.challengeData!!)
                else -> NotificationAdditionalData.Unknown
            }
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

    private fun mapNotificationTransactionData(from: NotificationTransactionDataEntity): NotificationAdditionalData.NotificationTransactionDataModel {
        return NotificationAdditionalData.NotificationTransactionDataModel(
            amount = from.amount,
            status = from.status,
            senderId = from.senderId ?: "Unknown",
            recipientId = from.recipientId,
            senderTgName = from.senderTgName ?: "Unknown",
            recipientTgName = from.recipientTgName ?: "Unknown",
            senderPhoto = from.senderPhoto,
            recipientPhoto = from.recipientPhoto,
            transactionId = from.transactionId,
            incomeTransaction = from.incomeTransaction
        )
    }

    private fun mapNotificationChallengeData(from: NotificationChallengeDataEntity): NotificationAdditionalData.NotificationChallengeDataModel {
        return NotificationAdditionalData.NotificationChallengeDataModel(
            challengeId = from.challengeId,
            challengeName = from.challengeName ?: "Unknown",
            creatorTgName = from.creatorTgName,
            creatorFirstName = from.creatorFirstName ?: "",
            creatorSurname = from.creatorSurname ?: "",
            creatorPhoto = from.creatorPhoto
        )
    }
}