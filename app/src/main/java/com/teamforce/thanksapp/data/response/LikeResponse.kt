package com.teamforce.thanksapp.data.response

data class LikeResponse(
    val id: Int,
    val transaction_id: Int?,
    val content_type_id: Int,
    val object_id: Int,
    val like_kind_id: Int,
    val isLiked: Boolean,
    val date_created: String,
    val date_deleted: String?,
    val user_id: Int
)
