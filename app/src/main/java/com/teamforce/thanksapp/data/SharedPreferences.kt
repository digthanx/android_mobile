package com.teamforce.thanksapp.data

import android.content.Context
import android.content.SharedPreferences
import com.teamforce.thanksapp.utils.UserDataRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun savePreferences(authtoken: String?, telegram: String?, username: String?) {
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
    }
}