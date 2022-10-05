package com.teamforce.thanksapp.domain.repositories

import com.teamforce.thanksapp.data.entities.profile.ProfileEntity
import com.teamforce.thanksapp.data.response.PutUserAvatarResponse
import com.teamforce.thanksapp.domain.models.profile.ProfileModel
import com.teamforce.thanksapp.utils.ResultWrapper

interface ProfileRepository {
    suspend fun loadUserProfile(): ResultWrapper<ProfileEntity>
    suspend fun updateUserAvatar(
        userId: String,
        filePath: String
    ): ResultWrapper<PutUserAvatarResponse>
}