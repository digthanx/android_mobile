package com.teamforce.thanksapp.data.entities.notifications

import com.google.gson.annotations.SerializedName

data class NotificationEntity(
    @SerializedName("id")
    val id: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("theme")
    val theme: String?,
    @SerializedName("read")
    val isRead: Boolean,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("transaction_data")
    val transactionData: NotificationTransactionDataEntity?,
    @SerializedName("challenge_data")
    val challengeData: NotificationChallengeDataEntity?
)

data class NotificationTransactionDataEntity(
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("sender_id")
    val senderId: String?,
    @SerializedName("recipient_id")
    val recipientId: Int,
    @SerializedName("sender_tg_name")
    val senderTgName: String?,
    @SerializedName("recipient_tg_name ")
    val recipientTgName: String?,
    @SerializedName("sender_photo")
    val senderPhoto: String?,
    @SerializedName("recipient_photo")
    val recipientPhoto: String?,
    @SerializedName("transaction_id")
    val transactionId: Int,
    @SerializedName("income_transaction")
    val incomeTransaction: Boolean,
)

data class NotificationChallengeDataEntity(
    @SerializedName("challenge_id")
    val challengeId: Int,
    @SerializedName("challenge_name")
    val challengeName: String?,
    @SerializedName("creator_tg_name")
    val creatorTgName: String,
    @SerializedName("creator_first_name")
    val creatorFirstName: String?,
    @SerializedName("creator_surname")
    val creatorSurname: String?,
    @SerializedName("creator_photo")
    val creatorPhoto: String?
)