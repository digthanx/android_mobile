package com.teamforce.thanksapp.domain.models.notifications

import java.util.*

sealed class NotificationItem(
) {
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
        val uuid: UUID = UUID.randomUUID()
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

    object Unknown : NotificationAdditionalData()

}

enum class NotificationType {
    Transaction,
    Challenge,
    Comment,
    Like,
    Unknown
}