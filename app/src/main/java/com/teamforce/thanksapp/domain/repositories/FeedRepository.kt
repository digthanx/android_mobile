package com.teamforce.thanksapp.domain.repositories

import androidx.paging.PagingData
import com.teamforce.thanksapp.data.response.FeedResponse
import com.teamforce.thanksapp.domain.models.feed.FeedModel
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    fun getFeed(
        mineOnly: Int?,
        publicOnly: Int?
    ): Flow<PagingData<FeedResponse>>

    fun getEvents(
    ): Flow<PagingData<FeedModel>>
}