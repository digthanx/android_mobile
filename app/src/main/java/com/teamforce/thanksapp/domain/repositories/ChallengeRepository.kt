package com.teamforce.thanksapp.domain.repositories

import com.teamforce.thanksapp.data.response.GetChallengeContendersResponse
import com.teamforce.thanksapp.utils.ResultWrapper

interface ChallengeRepository {

    suspend fun loadContenders(challengeId: Int): ResultWrapper<GetChallengeContendersResponse>

}