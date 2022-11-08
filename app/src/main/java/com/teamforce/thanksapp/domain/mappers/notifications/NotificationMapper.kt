package com.teamforce.thanksapp.domain.mappers.notifications

import com.teamforce.thanksapp.data.entities.notifications.*
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
                NotificationType.Comment -> mapNotificationCommentData(from.commentData!!)
                NotificationType.Report -> mapNotificationChallengeReportData(from.reportData!!)
                NotificationType.ChallengeWinner -> mapNotificationChallengeWinnerData(from.winnerData!!)
                NotificationType.Like -> mapNotificationReactionData(from.likeData!!)
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
            "w" -> NotificationType.ChallengeWinner
            "r" -> NotificationType.Report
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

    private fun mapNotificationChallengeReportData(from: NotificationChallengeReportData): NotificationAdditionalData.NotificationChallengeReportDataModel {
        return NotificationAdditionalData.NotificationChallengeReportDataModel(
            reportId = from.reportId,
            challengeId = from.challengeId,
            challengeName = from.challengeName ?: "Unknown",
            reportSenderPhoto = from.reportSenderPhoto,
            reportSenderSurname = from.reportSenderSurname ?: "",
            reportSenderTgName = from.reportSenderTgName ?: "Unknown",
            reportSenderFirstName = from.reportSenderFirstName ?: " "
        )
    }

    private fun mapNotificationChallengeWinnerData(from: NotificationChallengeWinnerData): NotificationAdditionalData.NotificationChallengeWinnerDataModel {
        return NotificationAdditionalData.NotificationChallengeWinnerDataModel(
            prize = from.prize,
            challengeId = from.challengeId,
            challengeName = from.challengeName ?: "Unknown",
            challengeReportId = from.challengeReportId
        )
    }

    private fun mapNotificationReactionData(from: NotificationReactionData): NotificationAdditionalData.NotificationReactionDataModel {
        return NotificationAdditionalData.NotificationReactionDataModel(
            transactionId = from.transactionId,
            commentId = from.commentId,
            challengeId = from.challengeId,
            reactionFromPhoto = from.reactionFromPhoto,
            reactionFromTgName = from.reactionFromTgName ?: "Unknown",
            reactionFromSurname = from.reactionFromSurname ?: "",
            reactionFromFirstName = from.reactionFromFirstName ?: ""
        )
    }

    private fun mapNotificationCommentData(from: NotificationCommentData): NotificationAdditionalData.NotificationCommentDataModel {
        return NotificationAdditionalData.NotificationCommentDataModel(
            transactionId = from.transactionId,
            challengeId = from.challengeId,
            commentFromPhoto = from.commentFromPhoto,
            commentFromFirstName = from.commentFromFirstName ?: "",
            commentFromSurname = from.commentFromSurname ?: "",
            commentFromTgName = from.commentFromTgName ?: "Unknown"
        )
    }
}