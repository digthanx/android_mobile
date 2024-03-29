package com.teamforce.thanksapp.data.repository

import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.entities.profile.OrganizationModel
import com.teamforce.thanksapp.data.entities.profile.ProfileEntity
import com.teamforce.thanksapp.data.request.ChangeOrgRequest
import com.teamforce.thanksapp.data.request.VerificationRequest
import com.teamforce.thanksapp.data.request.VerificationRequestForChangeOrg
import com.teamforce.thanksapp.data.response.ChangeOrgResponse
import com.teamforce.thanksapp.data.response.ProfileResponse
import com.teamforce.thanksapp.data.response.PutUserAvatarResponse
import com.teamforce.thanksapp.data.response.VerificationResponse
import com.teamforce.thanksapp.domain.repositories.ProfileRepository
import com.teamforce.thanksapp.utils.ResultWrapper
import com.teamforce.thanksapp.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val thanksApi: ThanksApi
) : ProfileRepository {
    override suspend fun loadUserProfile(): ResultWrapper<ProfileEntity> {
        return safeApiCall(Dispatchers.IO) {
            thanksApi.getProfile()
        }
    }

    override suspend fun updateUserAvatar(
        userId: String,
        filePath: String,
        filePathCropped: String
    ): ResultWrapper<PutUserAvatarResponse> {
        return safeApiCall(Dispatchers.IO) {
            val file = File(filePath)
            val fileCropped = File(filePathCropped)
            val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val requestFileCropped = RequestBody.create(MediaType.parse("multipart/form-data"), fileCropped)
            val bodyCropped = MultipartBody.Part.createFormData("cropped_photo", fileCropped.name, requestFileCropped)
            val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
            thanksApi.putUserAvatar(userId, body, bodyCropped)
            // Отправляю ориг и обрезку на бек, протестировать с Андреем
        }
    }

    override suspend fun getOrganizations(): ResultWrapper<List<OrganizationModel>> {
        return safeApiCall(Dispatchers.IO) {
            thanksApi.getOrganizations()
        }
    }

    override suspend fun changeOrganization(organizationId: Int):  Call<ChangeOrgResponse> {
        return thanksApi.changeOrganization(ChangeOrgRequest(organizationId))
    }

    override suspend fun changeOrganizationVerifyWithTelegram(
        xId: String,
        xCode: String,
        code: String,
        orgCode: String
    ): ResultWrapper<VerificationResponse> {
        return safeApiCall(Dispatchers.IO){
            thanksApi.changeOrganizationVerifyWithTelegram(
                xId = xId,
                xCode = xCode,
                orgCode = orgCode,
                VerificationRequestForChangeOrg(code = code)
            )
        }
    }

//    override suspend fun changeOrganizationVerifyWithEmail(
//        xEmail: String,
//        xCode: String,
//        code: String
//    ): ResultWrapper<VerificationResponse> {
//        return safeApiCall(Dispatchers.IO){
//            thanksApi.changeOrganizationVerifyWithEmail(
//                xEmail = xEmail,
//                xCode = xCode,
//                VerificationRequest(code = code)
//            )
//        }
//    }


}