package com.teamforce.thanksapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.response.FeedResponse
import com.teamforce.thanksapp.data.sources.feed.FeedPagingSource
import com.teamforce.thanksapp.domain.repositories.FeedRepository
import com.teamforce.thanksapp.utils.Consts
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FeedRepositoryImpl @Inject constructor(
    private val thanksApi: ThanksApi
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
}