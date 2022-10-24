package com.teamforce.thanksapp.domain.models.feed

import com.google.android.datatransport.cct.StringMerger

sealed class FeedModel {
    data class ChallengeFeedEvent(
        val id: Int,
        val commentAmount: Int?,
        val eventObjectId: Int?,
        val eventRecordId: Int?,
        val likesAmount: Int?,
        val time: String,
        val userId: Int?,
        val challengeCreatedAt: String,
        val challengeCreatorFirstName: String,
        val challengeCreatorSurname: String,
        val challengeCreatorId: Int,
        val challengeCreatorTgName: String?,
        val challengeId: Int,
        val challengeName: String,
        val challengePhoto: String?,
        val userLiked: Boolean
    ) : FeedModel()

    data class TransactionFeedEvent(
        val id: Int,
        val commentAmount: Int?,
        val eventObjectId: Int?,
        val eventRecordId: Int?,
        val likesAmount: Int?,
        val time: String,
        val userId: Int?,
        val transactionAmount: Int,
        val transactionId: Int,
        val transactionIsAnonymous: Boolean,
        val transactionRecipientId: Int,
        val transactionRecipientPhoto: String?,
        val transactionRecipientTgName: String?,
        val transactionSenderId: Int?,
        val transactionSenderTgName: String?,
        val userLiked: Boolean,
        val transactionUpdatedAt: String,
        val transactionTags: List<String>
    ) : FeedModel()

    data class WinnerFeedEvent(
        val id: Int,
        val commentAmount: Int?,
        val eventObjectId: Int?,
        val eventRecordId: Int?,
        val likesAmount: Int?,
        val time: String,
        val userId: Int?,
        val challengeName: String,
        val reportId: Int,
        val updatedAt: String,
        val userLiked: Boolean,
        val winnerFirstName: String?,
        val winnerId: Int,
        val winnerPhoto: String?,
        val winnerSurname: String?,
        val winnerTgName: String?
        ) : FeedModel()
}
