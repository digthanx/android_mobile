package com.teamforce.thanksapp.data.response

import com.teamforce.thanksapp.model.domain.ChallengeModel

data class GetChallengesResponse(
    val challenges: List<ChallengeModel>
)
