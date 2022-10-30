package com.teamforce.thanksapp.domain.repositories

import androidx.paging.PagingData
import com.teamforce.thanksapp.data.response.FeedResponse
import com.teamforce.thanksapp.domain.models.feed.FeedItemByIdModel
import com.teamforce.thanksapp.domain.models.feed.FeedModel
import com.teamforce.thanksapp.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    fun getFeed(
        mineOnly: Int?,
        publicOnly: Int?
    ): Flow<PagingData<FeedResponse>>

    fun getEvents(): Flow<PagingData<FeedModel>>

    fun getWinners(): Flow<PagingData<FeedModel>>

    fun getChallenges(): Flow<PagingData<FeedModel>>
    
    fun getTransactions(): Flow<PagingData<FeedModel>>

    suspend fun getTransactionById(transactionId: Int): ResultWrapper<FeedItemByIdModel>
}