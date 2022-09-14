package com.teamforce.thanksapp.data.response

import com.google.gson.annotations.SerializedName
import com.teamforce.thanksapp.model.domain.TagModel

data class FeedResponse(
    val id: Int,
    val time: String,
    val event_type: EventType,
    val transaction: Transaction,
    val scope: String

){
    data class EventType(
        val id: Int,
        val name: String,
        val object_type: String,
        val is_personal: Boolean,
        val has_scope: Boolean
    )

    data class Transaction(
        val id: Int,
        val sender: String,
        val recipient: String,
        val recipient_photo: String?,
        val recipient_first_name: String,
        val recipient_surname: String,
        val amount: String,
        val status: String,
        @SerializedName("is_anonymous")
        val is_anon: Boolean,
        val reason: String,
        val photo: String?,
        val tags: List<TagModel>?
    )
}
