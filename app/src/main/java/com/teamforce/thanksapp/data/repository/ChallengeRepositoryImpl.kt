package com.teamforce.thanksapp.data.repository

import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.request.CreateReportRequest
import com.teamforce.thanksapp.data.response.*
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
        reportId: Int,
        state: Map<String, Char>
    ): ResultWrapper<CheckReportResponse> {
        return safeApiCall(Dispatchers.IO) {
            thanksApi.checkChallengeReport(reportId, state)
        }
    }

    override suspend fun loadWinners(
        challengeId: Int): ResultWrapper<List<GetChallengeWinnersResponse.Winner>> {
        return safeApiCall(Dispatchers.IO) {
            thanksApi.getChallengeWinners(challengeId)
        }
    }

    override suspend fun loadChallengeResult(challengeId: Int): ResultWrapper<GetChallengeResultResponse> {
        return safeApiCall(Dispatchers.IO) {
            thanksApi.getChallengeResult(challengeId)
        }
    }
}
