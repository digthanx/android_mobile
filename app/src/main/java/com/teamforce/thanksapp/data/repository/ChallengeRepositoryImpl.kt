package com.teamforce.thanksapp.data.repository

import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.request.CreateReportRequest
import com.teamforce.thanksapp.data.response.CheckReportResponse
import com.teamforce.thanksapp.data.response.CreateReportResponse
import com.teamforce.thanksapp.data.response.GetChallengeContendersResponse
import com.teamforce.thanksapp.domain.repositories.ChallengeRepository
import com.teamforce.thanksapp.utils.ResultWrapper
import com.teamforce.thanksapp.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class ChallengeRepositoryImpl @Inject constructor(
    private val thanksApi: ThanksApi
) : ChallengeRepository {

    override suspend fun loadContenders(
        challengeId: Int
    ): ResultWrapper<List<GetChallengeContendersResponse.Contender>> {
        return safeApiCall(Dispatchers.IO) {
            thanksApi.getChallengeContenders(challengeId)
        }
    }

    override suspend fun createReport(
        photo: MultipartBody.Part?,
        challengeId: RequestBody,
        comment: RequestBody
    ): ResultWrapper<CreateReportResponse> {
        return safeApiCall(Dispatchers.IO) {
            thanksApi.createChallengeReport(photo, challengeId, comment)
        }
    }

    override suspend fun checkChallengeReport(
        challengeId: Int,
        state: Char
    ): ResultWrapper<CheckReportResponse> {
        return safeApiCall(Dispatchers.IO) {
            thanksApi.checkChallengeReport(challengeId, state)
        }
    }
}
