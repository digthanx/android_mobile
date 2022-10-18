package com.teamforce.thanksapp.domain.repositories

import androidx.paging.PagingData
import com.teamforce.thanksapp.data.response.*
import com.teamforce.thanksapp.model.domain.CommentModel
import com.teamforce.thanksapp.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow
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

    fun loadChallengeComments(
        challengeId: Int
    ): Flow<PagingData<CommentModel>>

    suspend fun createChallengeComment(
        challenge_id: Int,
        text: String
    ): ResultWrapper<CancelTransactionResponse>

    suspend fun deleteChallengeComment(
        commentId: Int
    ): ResultWrapper<CancelTransactionResponse>

}