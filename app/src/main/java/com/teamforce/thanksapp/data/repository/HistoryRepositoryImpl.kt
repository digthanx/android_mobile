package com.teamforce.thanksapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.request.CancelTransactionRequest
import com.teamforce.thanksapp.data.response.CancelTransactionResponse
import com.teamforce.thanksapp.data.response.HistoryItem
import com.teamforce.thanksapp.data.sources.history.HistoryPagingSource
import com.teamforce.thanksapp.domain.repositories.HistoryRepository
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.ResultWrapper
import com.teamforce.thanksapp.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
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

    override suspend fun cancelTransaction(
        id: String,
        status: CancelTransactionRequest
    ): ResultWrapper<CancelTransactionResponse> {
        return safeApiCall(Dispatchers.IO) {
            thanksApi.cancelTransaction(id, status)
        }
    }

}