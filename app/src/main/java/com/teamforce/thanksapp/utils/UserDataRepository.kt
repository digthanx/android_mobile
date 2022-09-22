package com.teamforce.thanksapp.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.teamforce.thanksapp.presentation.viewmodel.LoginViewModel

class UserDataRepository private constructor() {

    var token: String? = null
    var leastCoins: Int? = null
    var username: String? = null
    var tgId: String? = null
    var statusResponseAuth: String? = null
    var verifyCode: String? = null
    var email: String? = null
    var profileId: String? = null

    fun saveCredentials(context: Context, authtoken: String?, telegram: String?, userName: String?) {
        savePreferences(context, authtoken, telegram, username)
        token = authtoken
        username = userName?.trim()
    }

    fun logout(context: Context) {
        savePreferences(context, null, null, null)
        token = null
        leastCoins = null
        username = null
        tgId = null
        statusResponseAuth = null
        verifyCode = null
        email = null
        profileId = null
        LoginViewModel.logout()
    }

    private fun savePreferences(context: Context, authtoken: String?, telegram: String?, username: String?) {
        val prefs: SharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(SP_ARG_TELEGRAM, telegram)
        editor.putString(SP_ARG_TOKEN, authtoken)
        editor.putString(SP_ARG_USERNAME, username)
        editor.apply()
    }

    companion object {
        private const val SP_NAME = "com.teamforce.thanksapp"
        private const val SP_ARG_TELEGRAM = "Telegram"
        private const val SP_ARG_TOKEN = "Token"
        private const val SP_ARG_USERNAME = "Username"
        private var instance: UserDataRepository? = null

        fun getInstance(): UserDataRepository? {
            if (instance == null) {
                instance = UserDataRepository()
            }

            return instance
        }
    }
}
