package com.teamforce.thanksapp.data.api

import com.teamforce.thanksapp.data.request.*
import com.teamforce.thanksapp.data.response.*
import okhttp3.MultipartBody
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
    fun getProfile(@Header("Authorization") token: String): Call<ProfileResponse>

    @GET("/user/balance/")
    fun getBalance(@Header("Authorization") token: String): Call<BalanceResponse>

    @POST("/search-user/")
    fun getUsersList(
        @Header("Authorization") token: String,
        @Body usersListRequest: UsersListRequest
    ): Call<List<UserBean>>

    @POST("/send-coins/")
    fun sendCoins(
        @Header("Authorization") token: String,
        @Body request: SendCoinsRequest
    ): Call<SendCoinsResponse>

    @GET("/user/transactions/")
    fun getUserTransactions(@Header("Authorization") token: String): Call<List<UserTransactionsResponse>>

    @GET("/feed/")
    fun getFeed(@Header("Authorization") token: String): Call<List<FeedResponse>>

    @POST("/users-list/")
    fun getUsersWithoutInput(
        @Header("Authorization") token: String,
        @Body get_users: UserListWithoutInputRequest
    ): Call<List<UserBean>>

    @Multipart
    @PUT("/update-profile-image/{id}/")
    fun putUserAvatar(
        @Header("Authorization") token: String,
        @Path("id") userId: String,
        @Part image: MultipartBody.Part
    ): Call<PutUserAvatarResponse>
}
