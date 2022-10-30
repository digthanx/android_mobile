package com.teamforce.thanksapp.domain.models.feed

import com.google.gson.annotations.SerializedName
import com.teamforce.thanksapp.data.entities.feed.Tag
import com.teamforce.thanksapp.model.domain.TagModel

data class FeedItemByIdModel(
    val id: Int,
    val sender_id: Int?,
    val sender_tg_name: String?,
    val sender_first_name: String?,
    val sender_surname: String?,
    val sender_photo: String?,
    val recipient_id: Int,
    val recipient_tg_name: String?,
    val recipient_first_name: String?,
    val recipient_surname: String?,
    val recipient_photo: String?,
    @SerializedName("tags")
    val tags: List<Tag>,
    val amount: Int,
    val updated_at: String,
    val reason: String?,
    val photo: String?,
    val user_liked: Boolean,
    val like_amount: Int,
    val is_anonymous: Boolean
)
