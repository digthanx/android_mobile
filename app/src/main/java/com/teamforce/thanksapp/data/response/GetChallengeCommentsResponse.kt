package com.teamforce.thanksapp.data.response

import com.teamforce.thanksapp.model.domain.CommentModel

data class GetChallengeCommentsResponse(
    val challenge_id: Int,
    val comments: List<CommentModel>
)
