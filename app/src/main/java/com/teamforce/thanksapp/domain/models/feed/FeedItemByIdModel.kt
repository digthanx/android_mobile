package com.teamforce.thanksapp.domain.models.feed

import com.google.gson.annotations.SerializedName
import com.teamforce.thanksapp.model.domain.TagModel

data class FeedItemByIdModel(
    val id: Int,
    val sender: String?,
    val recipient: String?,
    val status: String,
    @SerializedName("transaction_class")
    val transactionClass: String,
    @SerializedName("expire_to_cancel")
    val expireToCancel: String?,
    @SerializedName("can_user_cancel")
    val canUserCancel: Boolean,
    val tags: List<TagModel>?,
    @SerializedName("reason_def")
    val reasonDef: String?,
    val amount: Int,
    val created_at: String,
    val updated_at: String,
    val reason: String?,
    val photo: String?
)
