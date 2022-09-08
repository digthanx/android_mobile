package com.teamforce.thanksapp.utils

import android.content.Context
import com.teamforce.thanksapp.data.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    var token: String? = null
    var leastCoins: Int? = null
    var username: String? = null
    var tgId: String? = null
    var statusResponseAuth: String? = null
    var verifyCode: String? = null
    var email: String? = null
    var profileId: String? = null

    fun saveCredentials(authtoken: String?, telegram: String?) {
       sharedPreferences.savePreferences(authtoken, telegram)
        token = authtoken
    }

    fun logout() {
        sharedPreferences.savePreferences(null, null)
        token = null
        leastCoins = null
        username = null
        tgId = null
        statusResponseAuth = null
        verifyCode = null
        email = null
        profileId = null
    }
}
