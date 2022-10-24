package com.teamforce.thanksapp.data.entities.feed


import com.google.gson.annotations.SerializedName

data class FeedItemEntity(
    @SerializedName("challenge")
    val challenge: Challenge?,//
    @SerializedName("comments_amount")
    val commentsAmount: Int?,
    @SerializedName("event_object_id")
    val eventObjectId: Int?,
    @SerializedName("event_record_id")
    val eventRecordId: Int?,
    @SerializedName("event_type_id")
    val eventTypeId: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("likes_amount")
    val likesAmount: Int?,
    @SerializedName("object_selector")
    val objectSelector: String?,// это тип
    @SerializedName("scope_id")
    val scopeId: Int?,
    @SerializedName("time")
    val time: String,
    @SerializedName("transaction")
    val transaction: Transaction?,
    @SerializedName("user_id")
    val userId: Int?,
    @SerializedName("winner")
    val winner: Winner?
)