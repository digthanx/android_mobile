package com.teamforce.thanksapp.domain.repositories

import androidx.paging.PagingData
import com.teamforce.thanksapp.data.response.CancelTransactionResponse
import com.teamforce.thanksapp.data.response.FeedResponse
import com.teamforce.thanksapp.data.response.LikeResponse
import com.teamforce.thanksapp.domain.models.feed.FeedItemByIdModel
import com.teamforce.thanksapp.domain.models.feed.FeedModel
import com.teamforce.thanksapp.model.domain.CommentModel
import com.teamforce.thanksapp.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    fun getFeed(
        mineOnly: Int?,
        publicOnly: Int?
    ): Flow<PagingData<FeedResponse>>

    fun getComments(
        transactionId: Int
    ): Flow<PagingData<CommentModel>>

    suspend fun createComment(
        transactionId: Int,
        text: String
    ): ResultWrapper<CancelTransactionResponse>

    suspend fun deleteComment(
        commentId: Int
    ): ResultWrapper<CancelTransactionResponse>

    fun getEvents(): Flow<PagingData<FeedModel>>

    fun getWinners(): Flow<PagingData<FeedModel>>

    fun getChallenges(): Flow<PagingData<FeedModel>>
    
    fun getTransactions(): Flow<PagingData<FeedModel>>

    suspend fun getTransactionById(transactionId: Int): ResultWrapper<FeedItemByIdModel>

    suspend fun pressLike(transactionId: Int): ResultWrapper<LikeResponse>
}