package com.teamforce.thanksapp.domain.repositories

import androidx.paging.PagingData
import com.teamforce.thanksapp.data.response.HistoryItem
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getHistory(
        receivedOnly: Int?,
        sentOnly: Int?
    ): Flow<PagingData<HistoryItem.UserTransactionsResponse>>
}