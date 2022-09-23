package com.teamforce.thanksapp.data.response

import com.teamforce.thanksapp.model.domain.CommentModel

data class GetCommentsResponse(
    val transaction_id: Int,
    val comments: List<CommentModel>
)
