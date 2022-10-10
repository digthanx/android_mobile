package com.teamforce.thanksapp.domain.repositories

import androidx.paging.PagingData
import com.teamforce.thanksapp.data.request.CancelTransactionRequest
import com.teamforce.thanksapp.data.response.CancelTransactionResponse
import com.teamforce.thanksapp.data.response.HistoryItem
import com.teamforce.thanksapp.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getHistory(
        receivedOnly: Int?,
        sentOnly: Int?
    ): Flow<PagingData<HistoryItem.UserTransactionsResponse>>

    suspend fun cancelTransaction(
        id: String,
        status: CancelTransactionRequest
    ): ResultWrapper<CancelTransactionResponse>
}