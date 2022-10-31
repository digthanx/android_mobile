package com.teamforce.thanksapp.data.api

import com.teamforce.thanksapp.data.entities.NotificationEntity
import com.teamforce.thanksapp.data.entities.PushTokenEntity
import com.teamforce.thanksapp.data.entities.RemovePushTokenEntity
import com.teamforce.thanksapp.data.entities.profile.ContactEntity
import com.teamforce.thanksapp.data.entities.profile.ProfileEntity
import com.teamforce.thanksapp.data.request.*
import com.teamforce.thanksapp.data.response.*
import com.teamforce.thanksapp.model.domain.ChallengeModel
import com.teamforce.thanksapp.model.domain.ChallengeModelById
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ThanksApi {

    @POST("/auth/")
    fun authorization(
        @Body authorizationRequest: AuthorizationRequest
    ): Call<Any>

    @POST("/verify/")
    fun verificationWithTelegram(
        @Header("X-Telegram") xId: String?,
        @Header("X-Code") xCode: String?,
        @Body verificationRequest: VerificationRequest
    ): Call<VerificationResponse>

    @POST("/verify/")
    fun verificationWithEmail(
        @Header("X-Email") xEmail: String?,
        @Header("X-Code") xCode: String?,
        @Body verificationRequest: VerificationRequest
    ): Call<VerificationResponse>

    @GET("/user/profile/")
    suspend fun getProfile(): ProfileEntity

    @GET("/user/balance/")
    fun getBalance(): Call<BalanceResponse>

    @POST("/search-user/")
    fun getUsersList(
        @Body usersListRequest: UsersListRequest
    ): Call<List<UserBean>>

    @POST("/send-coins/")
    fun sendCoins(
        @Body request: SendCoinsRequest
    ): Call<SendCoinsResponse>

    @Multipart
    @POST("/send-coins/")
    fun sendCoinsWithImage(
        @Part photo: MultipartBody.Part?,
        @Part("recipient") recipient: RequestBody,
        @Part("amount") amount: RequestBody,
        @Part("reason") reason: RequestBody,
        @Part("is_anonymous") is_anonymous: RequestBody,
        @Part("tags") tags: RequestBody?
    ): Call<SendCoinsResponse>

    @GET("/user/transactions/")
    suspend fun getUserTransactions(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("sent_only") sentOnly: Int?,
        @Query("received_only") receivedOnly: Int?
    ): List<HistoryItem.UserTransactionsResponse>

    @GET("/feed/")
    fun getFeed(): Call<List<FeedResponse>>

    @POST("/users-list/")
    fun getUsersWithoutInput(
        @Body get_users: UserListWithoutInputRequest
    ): Call<List<UserBean>>


    @Multipart
    @POST("/update-profile-image/{id}/")
    suspend fun putUserAvatar(
        @Path("id") userId: String,
        @Part photo: MultipartBody.Part,
        @Part cropped_photo: MultipartBody.Part
    ): PutUserAvatarResponse


    @PUT("/cancel-transaction/{id}/")
    suspend fun cancelTransaction(
        @Path("id") transactionId: String,
        @Body status: CancelTransactionRequest
    ): CancelTransactionResponse


    @PUT("/update-profile-by-user/{id}/")
    fun updateProfile(
        @Path("id") userId: String,
        @Body data: UpdateProfileRequest
    ): Call<UpdateProfileResponse>

    @POST("/create-few-contacts/")
    fun updateFewContact(
        @Body data: List<ContactEntity>?,
    ): Call<UpdateFewContactsResponse>

    @GET("/send-coins-settings/")
    fun getTags(
    ): Call<GetTagsResponse>

    @GET("/profile/{user_id}/")
    fun getAnotherProfile(
        @Path("user_id") user_Id: Int
    ): Call<ProfileResponse>

    @POST("/press-like/")
    fun pressLike(
        @Body data: Map<String, Int>
    ): Call<CancelTransactionResponse>

    @POST("/get-comments/")
    fun getComments(
        @Body transaction_id: GetCommentsRequest
    ): Call<GetCommentsResponse>

    @POST("/create-comment/")
    fun createComment(
        @Body data: CreateCommentRequest
    ): Call<CancelTransactionResponse>

    @DELETE("/delete-comment/{comment_id}/")
    fun deleteComment(
        @Path("comment_id") commentId: Int
    ): Call<CancelTransactionResponse>

    @Multipart
    @POST("/create-challenge/")
    fun createChallenge(
        @Part photo: MultipartBody.Part?,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("end_at") endAt: RequestBody?,
        @Part("start_balance") amountFund: RequestBody,
        @Part("parameter_id") parameter_id: RequestBody,
        @Part("parameter_value") parameter_value: RequestBody,
    ): Call<ChallengeModel>

    @GET("/challenges/")
    suspend fun getChallenges(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): List<ChallengeModel>

    @GET("/challenges/{challenge_id}/")
    fun getChallenge(
        @Path("challenge_id") commentId: Int
    ): Call<ChallengeModelById>

    @Multipart
    @POST("/create-challenge-report/")
    suspend fun createChallengeReport(
        @Part photo: MultipartBody.Part?,
        @Part("challenge") challengeId: RequestBody,
        @Part("text") comment: RequestBody
    ): CreateReportResponse

    @GET("/challenge-contenders/{challenge_id}/")
    suspend fun getChallengeContenders(
        @Path("challenge_id") challengeId: Int
    ): List<GetChallengeContendersResponse.Contender>

    @PUT("/check-challenge-report/{challenge_id}/")
    suspend fun checkChallengeReport(
        @Path("challenge_id") challengeId: Int,
        @Body data: CheckChallengeReportRequest?
    ): CheckReportResponse

    @GET("/challenge-winners-reports/{challenge_id}/")
    suspend fun getChallengeWinners(
        @Path("challenge_id") challengeId: Int,
    ): List<GetChallengeWinnersResponse.Winner>

    @GET("/challenge-result/{challenge_id}/")
    suspend fun getChallengeResult(
        @Path("challenge_id") challengeId: Int
    ): List<GetChallengeResultResponse>

    @POST("/get-comments/")
    suspend fun getChallengeComments(
        @Body challenge_id: GetChallengeCommentsRequest
    ): GetChallengeCommentsResponse

    @POST("/create-comment/")
    suspend fun createChallengeComment(
        @Body data: CreateChallengeCommentRequest
    ): CancelTransactionResponse

    @DELETE("/delete-comment/{comment_id}/")
    suspend fun deleteChallengeComment(
        @Path("comment_id") commentId: Int
    ): CancelTransactionResponse

    @GET("/challenge-report/{challenge_report_id}/")
    suspend fun getChallengeWinnerReportDetails(
        @Path("challenge_report_id") challengeReportId: Int,
    ): GetChallengeWinnersReportDetailsResponse

    @POST("/set-fcm-token/")
    suspend fun setPushToken(@Body token: PushTokenEntity): PushTokenEntity

    @POST("/remove-fcm-token/")
    suspend fun removePushToken(@Body remove: RemovePushTokenEntity)

    @GET("/notifications/")
    suspend fun getNotifications(): List<NotificationEntity>


}
