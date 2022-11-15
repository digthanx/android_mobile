package com.teamforce.thanksapp.data.repository

import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.entities.profile.OrganizationModel
import com.teamforce.thanksapp.data.entities.profile.ProfileEntity
import com.teamforce.thanksapp.data.response.ProfileResponse
import com.teamforce.thanksapp.data.response.PutUserAvatarResponse
import com.teamforce.thanksapp.domain.repositories.ProfileRepository
import com.teamforce.thanksapp.utils.ResultWrapper
import com.teamforce.thanksapp.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
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


}