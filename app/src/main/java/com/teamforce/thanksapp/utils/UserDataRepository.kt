package com.teamforce.thanksapp.utils

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataRepository @Inject constructor(
    private val sharedPreferences: com.teamforce.thanksapp.data.SharedPreferences
) {

    fun saveCredentialsForChangeOrg(
        xCode: String?,
        orgCode: String?,
        xId: String?,
    ) {
        sharedPreferences.savePreferencesForChangeOrg(
            xCode = xCode,
            xId = xId,
            orgCode = orgCode
        )
    }


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

    fun getXcode(): String? = sharedPreferences.xCode
    fun getOrgCode(): String? = sharedPreferences.orgCode
    fun getXid(): String? = sharedPreferences.xId

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
