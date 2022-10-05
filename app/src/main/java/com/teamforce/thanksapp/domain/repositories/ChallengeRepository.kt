package com.teamforce.thanksapp.domain.repositories

import com.teamforce.thanksapp.data.request.CreateReportRequest
import com.teamforce.thanksapp.data.response.CreateReportResponse
import com.teamforce.thanksapp.data.response.GetChallengeContendersResponse
import com.teamforce.thanksapp.utils.ResultWrapper
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ChallengeRepository {

    suspend fun loadContenders(challengeId: Int): ResultWrapper<GetChallengeContendersResponse>

    suspend fun createReport(
        photo: MultipartBody.Part?,
        challengeId: RequestBody,
        comment: RequestBody
    ): ResultWrapper<CreateReportResponse>

}