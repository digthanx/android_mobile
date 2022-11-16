package com.teamforce.thanksapp.domain.repositories

import com.teamforce.thanksapp.data.entities.profile.OrganizationModel
import com.teamforce.thanksapp.data.entities.profile.ProfileEntity
import com.teamforce.thanksapp.data.response.ChangeOrgResponse
import com.teamforce.thanksapp.data.response.PutUserAvatarResponse
import com.teamforce.thanksapp.data.response.VerificationResponse
import com.teamforce.thanksapp.domain.models.profile.ProfileModel
import com.teamforce.thanksapp.utils.ResultWrapper
import retrofit2.Call

interface ProfileRepository {
    suspend fun loadUserProfile(): ResultWrapper<ProfileEntity>
    suspend fun updateUserAvatar(
        userId: String,
        filePath: String,
        filePathCropped: String
    ): ResultWrapper<PutUserAvatarResponse>

    suspend fun getOrganizations(): ResultWrapper<List<OrganizationModel>>

    suspend fun changeOrganization(organizationId: Int): Call<ChangeOrgResponse>

    suspend fun changeOrganizationVerifyWithTelegram(
        xId: String, xCode: String, code: String, orgCode: String): ResultWrapper<VerificationResponse>

//    suspend fun changeOrganizationVerifyWithEmail(
//        xEmail: String, xCode: String, code: String):ResultWrapper<VerificationResponse>
}