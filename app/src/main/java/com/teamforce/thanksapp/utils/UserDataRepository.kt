package com.teamforce.thanksapp.utils

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataRepository @Inject constructor(
    private val sharedPreferences: com.teamforce.thanksapp.data.SharedPreferences
) {
    fun saveCredentials(
        authToken: String?,
        telegram: String?,
        userName: String?,
    ) {
        sharedPreferences.savePreferences(
            authToken = authToken,
            telegram = telegram,
            username = userName,
        )
    }

    fun saveUsername(userName: String?) {
        sharedPreferences.username = userName
    }

    fun getUserName(): String? {
        return sharedPreferences.username
    }

    fun saveProfileId(userId: String?) {
        sharedPreferences.profileId = userId
    }

    fun getProfileId(): String? {
        return sharedPreferences.profileId;
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getToken()
    }

    fun logout() {
        sharedPreferences.logout()
    }
}
