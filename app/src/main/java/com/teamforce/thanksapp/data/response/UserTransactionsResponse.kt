package com.teamforce.thanksapp.data.response

import com.google.gson.annotations.SerializedName
import com.teamforce.thanksapp.model.domain.TagModel

class UserTransactionsResponse(
    val id: Int,
    val sender: Sender,
    val sender_id: Int,
    val recipient: Recipient,
    val recipient_id: Int,
    val transaction_status: TransactionStatus,
    @SerializedName("transaction_class") val transactionClass: TransactionClass,
    @SerializedName("expire_to_cancel") val expireToCancel: String,
    val amount: String,
    @SerializedName("can_user_cancel") val canUserCancel: Boolean,
    @SerializedName("tags") val tags : List<TagModel>,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    val reason: String,
    val photo: String?
){
    data class Sender(
        val sender_id: Int,
        val sender_tg_name: String,
        val sender_first_name: String,
        val sender_surname: String,
        val sender_photo: String?

    )

    data class Recipient(
        val recipient_id: Int,
        val recipient_tg_name: String,
        val recipient_first_name: String,
        val recipient_surname: String,
        val recipient_photo: String?
    )

    data class TransactionStatus(
        val id: String,
        @SerializedName("name")
        val transactionStatus: String
    )

    data class TransactionClass(
        val id: String,
        @SerializedName("name")
        val transactionThanks: String
    )
}

/*
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
 */
