package com.teamforce.thanksapp.data.request

data class GetReactionsForTransactionRequest(
    val transaction_id: Int,
    val offset: Int,
    val limit: Int,
    val include_name: Boolean = true,
    val include_code: Boolean = true
)
