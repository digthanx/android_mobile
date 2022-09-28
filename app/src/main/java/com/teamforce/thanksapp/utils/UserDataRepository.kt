package com.teamforce.thanksapp.utils

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataRepository @Inject constructor(
    private val sharedPreferences: com.teamforce.thanksapp.data.SharedPreferences
) {

    var token: String? = null
    var leastCoins: Int? = null  // можно вынести в shared preferences
    var username: String? = null
    var email: String? = null
    var profileId: String? = null

    fun saveCredentials(
        authToken: String?,
        telegram: String?,
        userName: String?,
//        email: String?,
//        userId: Int
    ) {
        sharedPreferences.savePreferences(
            authToken = authToken,
            telegram = telegram,
            username = username,
//            email = email,
//            userId = userId
        )
        token = authToken
        username = userName?.trim()
    }

    fun logout() {
        sharedPreferences.savePreferences(null, null, null,/* null, -1*/)
        token = null
        leastCoins = null
        username = null
        email = null
        profileId = null
    }
}
