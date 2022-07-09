package com.teamforce.thanksapp.data.api

import com.teamforce.thanksapp.data.request.AuthorizationRequest
import com.teamforce.thanksapp.data.request.SendCoinsRequest
import com.teamforce.thanksapp.data.request.UsersListRequest
import com.teamforce.thanksapp.data.request.VerificationRequest
import com.teamforce.thanksapp.data.response.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ThanksApi {

    @POST("/auth/")
    fun authorization(
        @Body authorizationRequest: AuthorizationRequest
    ): Call<Any>

    @POST("/verify/")
    fun verification(
        @Header("X-ID") xId: String?,
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
}
