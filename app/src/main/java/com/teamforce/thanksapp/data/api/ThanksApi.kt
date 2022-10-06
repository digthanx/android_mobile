package com.teamforce.thanksapp.data.api

import com.teamforce.thanksapp.data.entities.profile.ContactEntity
import com.teamforce.thanksapp.data.entities.profile.ProfileEntity
import com.teamforce.thanksapp.data.network.models.Contact
import com.teamforce.thanksapp.data.request.*
import com.teamforce.thanksapp.data.response.*
import com.teamforce.thanksapp.model.domain.ChallengeModel
import com.teamforce.thanksapp.model.domain.ChallengeModelById
import com.teamforce.thanksapp.model.domain.TagModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.io.File

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
    fun getUserTransactions(): Call<List<UserTransactionsResponse>>

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
        @Part photo: MultipartBody.Part
    ): PutUserAvatarResponse


    @PUT("/cancel-transaction/{id}/")
    fun cancelTransaction(
        @Path("id") transactionId: String,
        @Body status: CancelTransactionRequest
    ): Call<CancelTransactionResponse>


    @PUT("/update-profile-by-user/{id}/")
    fun updateProfile(
        @Path("id") userId: String,
        @Body data: UpdateProfileRequest
    ): Call<UpdateProfileResponse>

    @POST("/create-few-contacts/")
    fun updateFewContact(
        @Body data: List<ContactEntity>?,
    ): Call<UpdateFewContactsResponse>

    @GET("/tags/")
    fun getTags(
    ): Call<List<TagModel>>

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
        @Part("end_at") endAt: RequestBody,
        @Part("start_balance") amountFund: RequestBody,
        @Part("parameter_id") parameter_id: RequestBody,
        @Part("parameter_value") parameter_value: RequestBody,
    ): Call<ChallengeModel>

    @GET("/challenges/")
    fun getChallenges(): Call<List<ChallengeModel>>

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
}
