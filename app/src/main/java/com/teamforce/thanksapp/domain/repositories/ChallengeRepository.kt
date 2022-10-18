package com.teamforce.thanksapp.domain.repositories

import com.teamforce.thanksapp.data.request.CreateChallengeCommentRequest
import com.teamforce.thanksapp.data.request.CreateReportRequest
import com.teamforce.thanksapp.data.response.*
import com.teamforce.thanksapp.utils.ResultWrapper
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ChallengeRepository {

    suspend fun loadContenders(challengeId: Int): ResultWrapper<List<GetChallengeContendersResponse.Contender>>

    suspend fun createReport(
        photo: MultipartBody.Part?,
        challengeId: RequestBody,
        comment: RequestBody
    ): ResultWrapper<CreateReportResponse>

    suspend fun checkChallengeReport(
        reportId: Int,
        state: Map<String, Char>,
        reasonOfReject: String?
    ): ResultWrapper<CheckReportResponse>

    suspend fun loadWinners(
        challengeId: Int,
    ): ResultWrapper<List<GetChallengeWinnersResponse.Winner>>

    suspend fun loadChallengeResult(
        challengeId: Int,
    ): ResultWrapper<List<GetChallengeResultResponse>>

    suspend fun loadChallengeComments(
        challengeId: Int
    ): ResultWrapper<GetChallengeCommentsResponse>

    suspend fun createChallengeComment(
        challenge_id: Int,
        text: String
    ): ResultWrapper<CancelTransactionResponse>

    suspend fun deleteChallengeComment(
        commentId: Int
    ): ResultWrapper<CancelTransactionResponse>

}