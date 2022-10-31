package com.teamforce.thanksapp.domain.models.notifications

data class NotificationModel(
    val type: NotificationType,
    val objectId: Int,
    val theme: String,
    val text: String,
    val isRead: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val id: Int
)

enum class NotificationType {
    transaction,
    challenge,
    comment,
    like,
    unknown
}