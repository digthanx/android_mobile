package com.teamforce.thanksapp.data.response

import com.google.gson.annotations.SerializedName

class UserTransactionsResponse(
    val id: Int,
    val sender: String,
    val recipient: String,
    val status: String,
    @SerializedName("transaction_class") val transactionClass: String,
    val amount: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    val reason: String
)
