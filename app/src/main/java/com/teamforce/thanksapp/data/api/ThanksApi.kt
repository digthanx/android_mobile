package com.teamforce.thanksapp.data.api

import com.teamforce.thanksapp.data.network.models.Contact
import com.teamforce.thanksapp.data.request.*
import com.teamforce.thanksapp.data.response.*
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

    @Multipart
    @POST("/send-coins/")
    fun sendCoinsWithImage(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part,
        @Part("recipient") recipient: RequestBody,
        @Part("amount") amount: RequestBody,
        @Part("reason") reason: RequestBody,
        @Part("is_anonymous") is_anonymous: RequestBody,
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
    @POST("/update-profile-image/{id}/")
    fun putUserAvatar(
        @Header("Authorization") token: String,
        @Path("id") userId: String,
        @Part photo: MultipartBody.Part
    ): Call<PutUserAvatarResponse>


    @PUT("/cancel-transaction/{id}/")
    fun cancelTransaction(
        @Header("Authorization") token: String,
        @Path("id") transactionId: String,
        @Body status: CancelTransactionRequest
    ): Call<CancelTransactionResponse>



    @PUT("/update-profile-by-user/{id}/")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Path("id") userId: String,
        @Body data: UpdateProfileRequest
    ): Call<UpdateProfileResponse>

    @PUT("/update-contact-by-user/{id}/")
    fun updateContact(
        @Header("Authorization") token: String,
        @Path("id") contactId: String,
        @Body data: UpdateContactRequest,
    ): Call<UpdateContactResponse>

    @POST("/create-contact-by-user/")
    fun createContact(
        @Header("Authorization") token: String,
        @Body data: CreateContactRequest?,
    ): Call<CreateContactResponse>

    @POST("/create-few-contacts/")
    fun updateFewContact(
        @Header("Authorization") token: String,
        @Body data: List<Contact>?,
    ): Call<UpdateFewContactsResponse>

}
