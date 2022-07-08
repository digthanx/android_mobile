package com.teamforce.thanksapp.model.domain

import com.teamforce.thanksapp.data.response.UserTransactionsResponse

class HistoryModel(
    var date: Int,
    var data: List<UserTransactionsResponse>
)
