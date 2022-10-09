package com.teamforce.thanksapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.response.HistoryItem
import com.teamforce.thanksapp.data.sources.history.HistoryPagingSource
import com.teamforce.thanksapp.domain.repositories.HistoryRepository
import com.teamforce.thanksapp.utils.Consts
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val thanksApi: ThanksApi
) : HistoryRepository {
    override fun getHistory(
        receivedOnly: Int?,
        sentOnly: Int?
    ): Flow<PagingData<HistoryItem.UserTransactionsResponse>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = Consts.PAGE_SIZE,
                prefetchDistance = 1,
                pageSize = Consts.PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                HistoryPagingSource(
                    api = thanksApi,
                    receivedOnly = receivedOnly,
                    sentOnly = sentOnly
                )
            }
        ).flow
    }
}