package com.teamforce.thanksapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.entities.feed.FeedItemEntity
import com.teamforce.thanksapp.data.request.CreateCommentRequest
import com.teamforce.thanksapp.data.response.CancelTransactionResponse
import com.teamforce.thanksapp.data.response.FeedResponse
import com.teamforce.thanksapp.data.response.GetReactionsForTransactionsResponse
import com.teamforce.thanksapp.data.response.LikeResponse
import com.teamforce.thanksapp.data.sources.createPager
import com.teamforce.thanksapp.data.sources.feed.FeedCommentsPagingSource
import com.teamforce.thanksapp.data.sources.feed.FeedPagingSource
import com.teamforce.thanksapp.data.sources.feed.FeedReactionsPagingSource
import com.teamforce.thanksapp.data.sources.feed.WhatExactlyWouldYouNeed
import com.teamforce.thanksapp.domain.mappers.feed.FeedMapper
import com.teamforce.thanksapp.domain.models.feed.FeedItemByIdModel
import com.teamforce.thanksapp.domain.models.feed.FeedModel
import com.teamforce.thanksapp.domain.repositories.FeedRepository
import com.teamforce.thanksapp.model.domain.CommentModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.ResultWrapper
import com.teamforce.thanksapp.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FeedRepositoryImpl @Inject constructor(
    private val thanksApi: ThanksApi,
    private val feedMapper: FeedMapper
) : FeedRepository {


    override fun getComments(
        transactionId: Int
    ): Flow<PagingData<CommentModel>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = Consts.PAGE_SIZE,
                prefetchDistance = 1,
                pageSize = Consts.PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                FeedCommentsPagingSource(
                    api = thanksApi,
                    transactionId = transactionId
                )
            }
        ).flow
    }

    override suspend fun createComment(
        transactionId: Int,
        text: String
    ): ResultWrapper<CancelTransactionResponse> {
        val data = CreateCommentRequest(transactionId, text)
        return safeApiCall(Dispatchers.IO){
            thanksApi.createComment(data)
        }
    }

    override suspend fun deleteComment(
        commentId: Int
    ): ResultWrapper<CancelTransactionResponse> {
        return safeApiCall(Dispatchers.IO){
            thanksApi.deleteComment(commentId)
        }
    }

    override fun getEvents(): Flow<PagingData<FeedModel>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = Consts.PAGE_SIZE,
                prefetchDistance = 5,
                pageSize = Consts.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FeedPagingSource(
                    api = thanksApi,
                    feedMapper = feedMapper,
                    whatWouldYouLikeToGet = WhatExactlyWouldYouNeed.EVENTS
                )
            }
        ).flow
    }

    override fun getWinners(): Flow<PagingData<FeedModel>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = Consts.PAGE_SIZE,
                prefetchDistance = 5,
                pageSize = Consts.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FeedPagingSource(
                    api = thanksApi,
                    feedMapper = feedMapper,
                    whatWouldYouLikeToGet = WhatExactlyWouldYouNeed.WINNERS
                )
            }
        ).flow
    }

    override fun getChallenges(): Flow<PagingData<FeedModel>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = Consts.PAGE_SIZE,
                prefetchDistance = 5,
                pageSize = Consts.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FeedPagingSource(
                    api = thanksApi,
                    feedMapper = feedMapper,
                    whatWouldYouLikeToGet = WhatExactlyWouldYouNeed.CHALLENGES
                )
            }
        ).flow
    }

    override fun getTransactions(): Flow<PagingData<FeedModel>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = Consts.PAGE_SIZE,
                prefetchDistance = 5,
                pageSize = Consts.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FeedPagingSource(
                    api = thanksApi,
                    feedMapper = feedMapper,
                    whatWouldYouLikeToGet = WhatExactlyWouldYouNeed.TRANSACTIONS
                )
            }
        ).flow
    }

    override suspend fun getTransactionById(transactionId: Int): ResultWrapper<FeedItemByIdModel> {
        val result = safeApiCall(Dispatchers.IO) {
            feedMapper.mapEntityByIdToModel(
                thanksApi.getTransactionById(transactionId.toString())
            )
        }
        return result
    }

    override suspend fun pressLike(transactionId: Int): ResultWrapper<LikeResponse> {
        val data = mapOf("like_kind" to 1, "transaction" to transactionId)
        return safeApiCall(Dispatchers.IO){
            thanksApi.newPressLike(data)
        }
    }

    override fun getReactions(
        transactionId: Int
    ): Flow<PagingData<GetReactionsForTransactionsResponse.InnerInfoLike>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = Consts.PAGE_SIZE,
                prefetchDistance = 1,
                pageSize = Consts.PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                FeedReactionsPagingSource(
                    api = thanksApi,
                    transactionId = transactionId
                )
            }
        ).flow

    }

}