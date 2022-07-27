package com.teamforce.thanksapp.model.domain

import com.teamforce.thanksapp.data.response.UserTransactionsResponse

data class HistoryModel(
    var date: Int,
    var data: List<UserTransactionsResponse>
)
