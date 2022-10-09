package com.teamforce.thanksapp.model.domain

import com.teamforce.thanksapp.data.response.HistoryItem


data class HistoryModel(
    var date: Int,
    var data: List<HistoryItem.UserTransactionsResponse>
)
