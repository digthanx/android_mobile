package com.teamforce.thanksapp.data.sources.feed

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.entities.feed.FeedItemEntity
import com.teamforce.thanksapp.data.response.FeedResponse
import com.teamforce.thanksapp.domain.mappers.feed.FeedMapper
import com.teamforce.thanksapp.domain.models.feed.FeedModel
import com.teamforce.thanksapp.utils.Consts
import retrofit2.HttpException
import java.io.IOException

class FeedPagingSource(
    private val api: ThanksApi,
    private val feedMapper: FeedMapper,
    private val whatWouldYouLikeToGet: WhatExactlyWouldYouNeed
) : PagingSource<Int, FeedModel>() {
    override fun getRefreshKey(state: PagingState<Int, FeedModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FeedModel> {
        var pageIndex = params.key ?: 1

        if (params is LoadParams.Refresh) {
            pageIndex = 1
        }

        return try {
            when(whatWouldYouLikeToGet){
                WhatExactlyWouldYouNeed.EVENTS -> {
                    val response = api.getEvents(
                        limit = Consts.PAGE_SIZE,
                        offset = pageIndex,
                    )
                    val nextKey =
                        if (response.isEmpty()) null
                        else {
                            pageIndex + (params.loadSize / Consts.PAGE_SIZE)
                        }
                    LoadResult.Page(
                        data = feedMapper.mapList(response),
                        prevKey = if (pageIndex == 1) null else pageIndex,
                        nextKey = nextKey
                    )
                }
                WhatExactlyWouldYouNeed.TRANSACTIONS -> {
                    val response = api.getEventsTransactions(
                        limit = Consts.PAGE_SIZE,
                        offset = pageIndex,
                    )
                    val nextKey =
                        if (response.isEmpty()) null
                        else {
                            pageIndex + (params.loadSize / Consts.PAGE_SIZE)
                        }
                    LoadResult.Page(
                        data = feedMapper.mapList(response),
                        prevKey = if (pageIndex == 1) null else pageIndex,
                        nextKey = nextKey
                    )
                }
                WhatExactlyWouldYouNeed.WINNERS -> {
                    val response = api.getEventsWinners(
                        limit = Consts.PAGE_SIZE,
                        offset = pageIndex,
                    )
                    val nextKey =
                        if (response.isEmpty()) null
                        else {
                            pageIndex + (params.loadSize / Consts.PAGE_SIZE)
                        }
                    LoadResult.Page(
                        data = feedMapper.mapList(response),
                        prevKey = if (pageIndex == 1) null else pageIndex,
                        nextKey = nextKey
                    )
                }
                else -> {
                    val response = api.getEventsChallenges(
                        limit = Consts.PAGE_SIZE,
                        offset = pageIndex,
                    )
                    val nextKey =
                        if (response.isEmpty()) null
                        else {
                            pageIndex + (params.loadSize / Consts.PAGE_SIZE)
                        }
                    LoadResult.Page(
                        data = feedMapper.mapList(response),
                        prevKey = if (pageIndex == 1) null else pageIndex,
                        nextKey = nextKey
                    )
                }
            }


        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}

enum class WhatExactlyWouldYouNeed(){
    EVENTS, TRANSACTIONS, WINNERS, CHALLENGES
}