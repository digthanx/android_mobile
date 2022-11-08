package com.teamforce.thanksapp.domain.models.notifications

import com.teamforce.thanksapp.data.entities.notifications.NotificationCommentData

sealed class NotificationItem {
    data class NotificationModel(
        val id: Int,
        val theme: String,
        val isRead: Boolean,
        val createdAt: String,
        val updatedAt: String,
        val data: NotificationAdditionalData
    ) : NotificationItem()

    data class DateTimeSeparator(
        val date: String,
    ) : NotificationItem()
}

sealed class NotificationAdditionalData {
    data class NotificationTransactionDataModel(
        val amount: Int,
        val status: String,
        val senderId: String,
        val recipientId: Int,
        val senderTgName: String,
        val recipientTgName: String,
        val senderPhoto: String?,
        val recipientPhoto: String?,
        val transactionId: Int,
        val incomeTransaction: Boolean,
    ) : NotificationAdditionalData()

    data class NotificationChallengeDataModel(
        val challengeId: Int,
        val challengeName: String,
        val creatorTgName: String,
        val creatorFirstName: String,
        val creatorSurname: String,
        val creatorPhoto: String?
    ) : NotificationAdditionalData()


    data class NotificationChallengeReportDataModel(
        val reportId: Int,
        val challengeId: Int,
        val challengeName: String,
        val reportSenderPhoto: String?,
        val reportSenderSurname: String,
        val reportSenderTgName: String,
        val reportSenderFirstName: String
    ) : NotificationAdditionalData()


    data class NotificationChallengeWinnerDataModel(
        val prize: Int,
        val challengeId: Int,
        val challengeName: String,
        val challengeReportId: Int
    ) : NotificationAdditionalData()

    data class NotificationReactionDataModel(
        val transactionId: Int?,
        val commentId: Int?,
        val challengeId: Int?,
        val reactionFromPhoto: String?,
        val reactionFromTgName: String,
        val reactionFromSurname: String,
        val reactionFromFirstName: String
    ) : NotificationAdditionalData()

    data class NotificationCommentDataModel(
        val transactionId: Int?,
        val challengeId: Int?,
        val commentFromPhoto: String?,
        val commentFromTgName: String,
        val commentFromSurname: String,
        val commentFromFirstName: String
    ) : NotificationAdditionalData()

    object Unknown : NotificationAdditionalData()
}

enum class NotificationType {
    Transaction,
    Challenge,
    Comment,
    Like,
    ChallengeWinner,
    Report,
    Unknown
}


