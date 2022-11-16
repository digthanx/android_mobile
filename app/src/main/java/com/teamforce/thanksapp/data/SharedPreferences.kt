package com.teamforce.thanksapp.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.teamforce.thanksapp.utils.UserDataRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun logout() {
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit {
            putString(SP_ARG_TELEGRAM, null)
            putString(SP_ARG_TOKEN, null)
            putString(SP_ARG_USERNAME, null)
            putString(SP_ARG_EMAIL, null)
            putString(SP_ARG_USER_ID, null)
            putString(SP_ARG_TG_CODE, null)
            putString(SP_ARG_X_CODE, null)
            putString(SP_ARG_ORG_CODE, null)
        }

    }

    fun savePreferences(
        authToken: String?,
        telegram: String?,
        username: String?,
    ) {
        val prefs: SharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(SP_ARG_TELEGRAM, telegram)
        editor.putString(SP_ARG_TOKEN, authToken)
        editor.putString(SP_ARG_USERNAME, username)
        editor.apply()
    }

    fun savePreferencesForChangeOrg(
        xCode: String?,
        orgCode: String?,
        xId: String?,
    ) {
        val prefs: SharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(SP_ARG_TG_CODE, xId)
        editor.putString(SP_ARG_X_CODE, xCode)
        editor.putString(SP_ARG_ORG_CODE, orgCode)
        editor.apply()
    }

    var xId: String?
        get() = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(
            SP_ARG_TG_CODE, null
        )
        set(value) = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit {
            putString(SP_ARG_TG_CODE, value)
        }

    var xCode: String?
        get() = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(
            SP_ARG_X_CODE, null
        )
        set(value) = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit {
            putString(SP_ARG_X_CODE, value)
        }

    var orgCode: String?
        get() = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(
            SP_ARG_ORG_CODE, null
        )
        set(value) = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit {
            putString(SP_ARG_ORG_CODE, value)
        }

    fun getToken(): String? {
        val prefs: SharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        return prefs.getString(SP_ARG_TOKEN, null)
    }

    var username: String?
        get() = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(
            SP_ARG_USERNAME, null
        )
        set(value) = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit {
            putString(SP_ARG_USERNAME, value)
        }

    var profileId: String?
        get() = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(
            SP_ARG_USER_ID, null
        )
        set(value) = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit {
            putString(SP_ARG_USER_ID, value)
        }

    var pushToken: String?
        get() = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(
            SP_ARG_PUSH_TOKEN, null
        )
        set(value) = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit {
            putString(SP_ARG_PUSH_TOKEN, value)
        }

    companion object {
        private const val SP_NAME = "com.teamforce.thanksapp"
        private const val SP_ARG_TELEGRAM = "Telegram"
        private const val SP_ARG_TOKEN = "Token"
        private const val SP_ARG_USERNAME = "Username"
        private const val SP_ARG_EMAIL = "Email"
        private const val SP_ARG_USER_ID = "UserId"
        private const val SP_ARG_PUSH_TOKEN = "pushToken"
        private const val SP_ARG_TG_CODE = "tgCode"
        private const val SP_ARG_X_CODE = "xCode"
        private const val SP_ARG_ORG_CODE = "orgCode"
    }
}