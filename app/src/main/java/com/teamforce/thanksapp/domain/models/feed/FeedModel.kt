package com.teamforce.thanksapp.domain.models.feed

sealed class FeedModel(
    open val id: Int,
) {
    data class ChallengeFeedEvent(
        override val id: Int,
        val commentAmount: Int?,
        val eventObjectId: Int?,
        val eventRecordId: Int?,
        val likesAmount: Int?,
        val time: String,
        val userId: Int?,
        val challengeCreatedAt: String?,
        val challengeEndAt: String?,
        val challengeCreatorFirstName: String,
        val challengeCreatorSurname: String,
        val challengeCreatorId: Int,
        val challengeCreatorTgName: String,
        val challengeId: Int,
        val challengeName: String,
        val challengePhoto: String?,
        val userLiked: Boolean
    ) : FeedModel(id)

    data class TransactionFeedEvent(
        override val id: Int,
        val commentAmount: Int?,
        val eventObjectId: Int?,
        val eventRecordId: Int?,
        var likesAmount: Int?,
        val time: String,
        val userId: Int?,
        val transactionAmount: Int,
        val transactionId: Int,
        val transactionIsAnonymous: Boolean,
        val transactionRecipientId: Int,
        val transactionRecipientPhoto: String?,
        val transactionRecipientTgName: String,
        val transactionSenderId: Int?,
        val transactionSenderTgName: String,
        var userLiked: Boolean,
        val transactionUpdatedAt: String,
        val transactionTags: List<String>,
        val isWithMe: Boolean,
        val isFromMe: Boolean
    ) : FeedModel(id)

    data class WinnerFeedEvent(
        override val id: Int,
        val commentAmount: Int?,
        val eventObjectId: Int?,
        val eventRecordId: Int?,
        val likesAmount: Int?,
        val time: String,
        val userId: Int?,
        val challengeId: Int,
        val challengeName: String,
        val reportId: Int,
        val updatedAt: String,
        val userLiked: Boolean,
        val winnerFirstName: String?,
        val winnerId: Int,
        val winnerPhoto: String?,
        val winnerSurname: String?,
        val winnerTgName: String
    ) : FeedModel(id)
}
