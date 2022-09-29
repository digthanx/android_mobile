package com.teamforce.thanksapp.utils

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataRepository @Inject constructor(
    private val sharedPreferences: com.teamforce.thanksapp.data.SharedPreferences
) {

    var leastCoins: Int? = null  // todo можно вынести в shared preferences
    var username: String? = null
    var email: String? = null // todo нужно вынести в shared preferences

    fun saveCredentials(
        authToken: String?,
        telegram: String?,
        userName: String?,
    ) {
        sharedPreferences.savePreferences(
            authToken = authToken,
            telegram = telegram,
            username = username,
        )
        username = userName?.trim()
    }

    fun saveUsername(userName: String?) {
        sharedPreferences.username = username
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
        sharedPreferences.savePreferences(null, null, null/* null, -1*/)
        sharedPreferences.username = null
        sharedPreferences.profileId = null
        leastCoins = null
        username = null
        email = null
    }
}
