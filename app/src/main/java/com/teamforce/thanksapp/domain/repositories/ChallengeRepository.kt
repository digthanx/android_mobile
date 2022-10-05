package com.teamforce.thanksapp.domain.repositories

import com.teamforce.thanksapp.data.response.GetChallengeParticipantsResponse
import com.teamforce.thanksapp.utils.ResultWrapper

interface ChallengeRepository {

    suspend fun loadParticipants(challengeId: Int): ResultWrapper<GetChallengeParticipantsResponse>

}