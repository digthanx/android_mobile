package com.teamforce.thanksapp.domain.repositories

import com.teamforce.thanksapp.data.response.ProfileResponse
import com.teamforce.thanksapp.data.response.PutUserAvatarResponse
import com.teamforce.thanksapp.utils.ResultWrapper

interface ProfileRepository {
    suspend fun loadUserProfile(): ResultWrapper<ProfileResponse>
    suspend fun updateUserAvatar(
        userId: String,
        filePath: String
    ): ResultWrapper<PutUserAvatarResponse>
}