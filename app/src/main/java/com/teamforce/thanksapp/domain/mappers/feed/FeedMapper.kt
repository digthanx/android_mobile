package com.teamforce.thanksapp.domain.mappers.feed

import android.content.Context
import android.util.Log
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.entities.feed.FeedItemByIdEntity
import com.teamforce.thanksapp.data.entities.feed.FeedItemEntity
import com.teamforce.thanksapp.domain.models.feed.FeedItemByIdModel
import com.teamforce.thanksapp.domain.models.feed.FeedModel
import com.teamforce.thanksapp.utils.UserDataRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class FeedMapper @Inject constructor(
    private val userDataRepository: UserDataRepository,
    @ApplicationContext private val context: Context
) {
    fun map(from: FeedItemEntity): FeedModel {
        return if (from.challenge != null) {
            FeedModel.ChallengeFeedEvent(
                id = from.id,
                commentAmount = from.commentsAmount,
                eventObjectId = from.eventObjectId,
                eventRecordId = from.eventRecordId,
                likesAmount = from.likesAmount,
                time = convertDateWithTime(from.time),
                userId = from.userId,
                challengeCreatedAt = convertDateWithoutTime(from.challenge.createdAt),
                challengeCreatorFirstName = from.challenge.creatorFirstName ?: "",
                challengeCreatorSurname = from.challenge.creatorSurname ?: "",
                challengeId = from.challenge.id,
                challengeCreatorTgName = from.challenge.creatorTgName ?: "anonymous",
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
                time = convertDateWithTime(from.time),
                userId = from.userId,
                transactionAmount = from.transaction.amount,
                transactionId = from.transaction.id,
                transactionIsAnonymous = from.transaction.isAnonymous,
                transactionRecipientId = from.transaction.recipientId,
                transactionRecipientPhoto = from.transaction.recipientPhoto,
                transactionRecipientTgName = from.transaction.recipientTgName,
                transactionSenderId = from.transaction.senderId,
                transactionSenderTgName = from.transaction.senderTgName ?: "anonymous",
                userLiked = from.transaction.userLiked,
                transactionUpdatedAt = from.transaction.updatedAt,
                transactionTags = from.transaction.tags.map {
                    it.name
                },
                isWithMe = (userDataRepository.getProfileId() == from.transaction.recipientId.toString() ||
                        userDataRepository.getProfileId() == from.transaction.senderId.toString()),
                isFromMe = userDataRepository.getProfileId() == from.transaction.senderId.toString()
            )
        } else {
            FeedModel.WinnerFeedEvent(
                id = from.id,
                commentAmount = from.commentsAmount,
                eventObjectId = from.eventObjectId,
                eventRecordId = from.eventRecordId,
                likesAmount = from.likesAmount,
                time = convertDateWithTime(from.time),
                userId = from.userId,
                challengeName = from.winner!!.challengeName ?: "Unknown",
                reportId = from.winner.id,
                updatedAt = from.winner.updatedAt,
                userLiked = from.winner.userLiked,
                winnerFirstName = from.winner.winnerFirstName,
                winnerId = from.winner.winnerId,
                winnerPhoto = from.winner.winnerPhoto,
                winnerSurname = from.winner.winnerSurname,
                winnerTgName = from.winner.winnerTgName ?: "tg_name_not_set"
            )
        }
    }

    fun mapList(from: List<FeedItemEntity>): List<FeedModel> {
        return from.map {
            map(it)
        }
    }

    fun mapEntityByIdToModel(from: FeedItemByIdEntity): FeedItemByIdModel {
        return FeedItemByIdModel(
            id = from.id,
            tags = from.tags,
            like_amount = from.like_amount,
            updated_at = convertDateWithTime(from.updated_at),
            reason = from.reason,
            photo = from.photo,
            sender_id = from.sender_id,
            sender_first_name = from.sender_first_name,
            sender_surname = from.sender_surname,
            sender_photo = from.sender_photo,
            sender_tg_name = from.sender_tg_name?: "anonymous",
            recipient_id = from.recipient_id,
            recipient_first_name = from.recipient_first_name,
            recipient_surname = from.recipient_surname,
            recipient_photo = from.recipient_photo,
            recipient_tg_name = from.recipient_tg_name?: "anonymous",
            is_anonymous = from.is_anonymous,
            user_liked = from.user_liked,
            amount = from.amount
        )
    }
    // Переводит в dd.mm.yyyy в hh:mm
    private fun convertDateWithTime(inputDate: String): String {
        try {
            val zdt: ZonedDateTime =
                ZonedDateTime.parse(inputDate, DateTimeFormatter.ISO_DATE_TIME)
            val dateTime: String? =
                LocalDateTime.parse(zdt.toString(), DateTimeFormatter.ISO_DATE_TIME)
                    .format(DateTimeFormatter.ofPattern("dd.MM.y HH:mm"))
            val date = dateTime?.subSequence(0, 10)
            val time = dateTime?.subSequence(11, 16)
            val today: LocalDate = LocalDate.now()
            val yesterday: String = today.minusDays(1).format(DateTimeFormatter.ISO_DATE)
            val todayString =
                LocalDate.parse(today.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .format(DateTimeFormatter.ofPattern("dd.MM.y"))
            val yesterdayString =
                LocalDate.parse(yesterday, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .format(DateTimeFormatter.ofPattern("dd.MM.y"))

            return when (date) {
                todayString -> {
                    String.format(
                        context.getString(R.string.dateTime),
                        "Сегодня",
                        time
                    )
                }
                yesterdayString -> {
                    String.format(
                        context.getString(R.string.dateTime),
                        "Вчера",
                        time
                    )
                }
                else -> {
                    String.format(context.getString(R.string.dateTime), date, time)
                }
            }

        } catch (e: Exception) {
            Log.e("HistoryAdapter", e.message, e.fillInStackTrace())
            return ""
        }
    }

    private fun convertDateWithoutTime(inputDate: String): String {
        try {
            val zdt: ZonedDateTime =
                ZonedDateTime.parse(inputDate, DateTimeFormatter.ISO_DATE_TIME)
            val dateTime: String? =
                LocalDateTime.parse(zdt.toString(), DateTimeFormatter.ISO_DATE_TIME)
                    .format(DateTimeFormatter.ofPattern("dd.MM.y HH:mm"))
            val date = dateTime?.subSequence(0, 10)
            val today: LocalDate = LocalDate.now()
            val yesterday: String = today.minusDays(1).format(DateTimeFormatter.ISO_DATE)
            val todayString =
                LocalDate.parse(today.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .format(DateTimeFormatter.ofPattern("dd.MM.y"))
            val yesterdayString =
                LocalDate.parse(yesterday, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .format(DateTimeFormatter.ofPattern("dd.MM.y"))

            if (date == todayString) {
                return "Сегодня"
            } else if (date == yesterdayString) {
                return "Вчера"
            } else {
                return date.toString()
            }
        } catch (e: Exception) {
            Log.e("ChallengeAdapter", e.message, e.fillInStackTrace())
            return "Не определено"
        }
    }
}