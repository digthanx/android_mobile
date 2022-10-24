package com.teamforce.thanksapp.domain.mappers.feed

import com.teamforce.thanksapp.data.entities.feed.FeedItemEntity
import com.teamforce.thanksapp.domain.models.feed.FeedModel
import javax.inject.Inject

class FeedMapper @Inject constructor() {
    fun map(from: FeedItemEntity): FeedModel {
        return if (from.challenge != null) {
            FeedModel.ChallengeFeedEvent(
                id = from.id,
                commentAmount = from.commentsAmount,
                eventObjectId = from.eventObjectId,
                eventRecordId = from.eventRecordId,
                likesAmount = from.likesAmount,
                time = from.time,
                userId = from.userId,
                challengeCreatedAt = from.challenge.createdAt,
                challengeCreatorFirstName = from.challenge.creatorFirstName ?: "",
                challengeCreatorSurname = from.challenge.creatorSurname ?: "",
                challengeId = from.challenge.creatorId,
                challengeCreatorTgName = from.challenge.creatorTgName,
                challengeName = from.challenge.name ?: "Unknown",
                challengePhoto = from.challenge.photo,
                userLiked = from.challenge.userLiked,
                challengeCreatorId = from.challenge.creatorId
            )
        } else if (from.transaction != null) {
            FeedModel.TransactionFeedEvent(
                id = from.id,
                commentAmount = from.commentsAmount,
                eventObjectId = from.eventObjectId,
                eventRecordId = from.eventRecordId,
                likesAmount = from.likesAmount,
                time = from.time,
                userId = from.userId,
                transactionAmount = from.transaction.amount,
                transactionId = from.transaction.id,
                transactionIsAnonymous = from.transaction.isAnonymous,
                transactionRecipientId = from.transaction.recipientId,
                transactionRecipientPhoto = from.transaction.recipientPhoto,
                transactionRecipientTgName = from.transaction.recipientTgName,
                transactionSenderId = from.transaction.senderId,
                transactionSenderTgName = from.transaction.senderTgName,
                userLiked = from.transaction.userLiked,
                transactionUpdatedAt = from.transaction.updatedAt,
                transactionTags = from.transaction.tags.map {
                    it.name
                }
            )
        } else {
            FeedModel.WinnerFeedEvent(
                id = from.id,
                commentAmount = from.commentsAmount,
                eventObjectId = from.eventObjectId,
                eventRecordId = from.eventRecordId,
                likesAmount = from.likesAmount,
                time = from.time,
                userId = from.userId,
                challengeName = from.winner!!.challengeName ?: "Unknown",
                reportId = from.winner.id,
                updatedAt = from.winner.updatedAt,
                userLiked = from.winner.userLiked,
                winnerFirstName = from.winner.winnerFirstName,
                winnerId = from.winner.winnerId,
                winnerPhoto = from.winner.winnerPhoto,
                winnerSurname = from.winner.winnerSurname,
                winnerTgName = from.winner.winnerTgName
            )
        }
    }
}