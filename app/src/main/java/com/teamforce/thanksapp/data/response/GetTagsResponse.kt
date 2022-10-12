package com.teamforce.thanksapp.data.response

import com.teamforce.thanksapp.model.domain.TagModel

data class GetTagsResponse(
    val tags: List<TagModel>
)
