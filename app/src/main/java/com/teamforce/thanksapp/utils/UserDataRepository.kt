package com.teamforce.thanksapp.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.teamforce.thanksapp.presentation.viewmodel.LoginViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataRepository @Inject constructor(
    private val sharedPreferences: com.teamforce.thanksapp.data.SharedPreferences
) {

    var token: String? = null
    var leastCoins: Int? = null
    var username: String? = null
    var tgId: String? = null
    var statusResponseAuth: String? = null
    var verifyCode: String? = null
    var email: String? = null
    var profileId: String? = null

    fun saveCredentials(
        context: Context,
        authtoken: String?,
        telegram: String?,
        userName: String?
    ) {
        sharedPreferences.savePreferences(authtoken, telegram, username)
        token = authtoken
        username = userName?.trim()
    }

    fun logout() {
        sharedPreferences.savePreferences(null, null, null)
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
