package com.teamforce.thanksapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.response.FeedResponse
import com.teamforce.thanksapp.data.sources.createPager
import com.teamforce.thanksapp.data.sources.feed.FeedPagingSource
import com.teamforce.thanksapp.domain.mappers.feed.FeedMapper
import com.teamforce.thanksapp.domain.models.feed.FeedModel
import com.teamforce.thanksapp.domain.repositories.FeedRepository
import com.teamforce.thanksapp.utils.Consts
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FeedRepositoryImpl @Inject constructor(
    private val thanksApi: ThanksApi,
    private val feedMapper: FeedMapper
) : FeedRepository {
    override fun getFeed(mineOnly: Int?, publicOnly: Int?): Flow<PagingData<FeedResponse>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = Consts.PAGE_SIZE,
                prefetchDistance = 1,
                pageSize = Consts.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FeedPagingSource(
                    api = thanksApi,
                    mineOnly = mineOnly,
                    publicOnly = publicOnly
                )
            }
        ).flow
    }

    override fun getEvents() = createPager { page ->
        val result = thanksApi.getEvents(limit = page, offset = Consts.PAGE_SIZE)
        feedMapper.mapList(result)
    }.flow

    override fun getWinners() = createPager { page ->
        val result = thanksApi.getEventsWinners(limit = page, offset = Consts.PAGE_SIZE)
        feedMapper.mapList(result)
    }.flow

    override fun getChallenges() = createPager { page ->
        val result = thanksApi.getEventsChallenges(limit = page, offset = Consts.PAGE_SIZE)
        feedMapper.mapList(result)
    }.flow

    override fun getTransactions() = createPager { page ->
        val result = thanksApi.getEventsTransactions(limit = page, offset = Consts.PAGE_SIZE)
        feedMapper.mapList(result)
    }.flow
}