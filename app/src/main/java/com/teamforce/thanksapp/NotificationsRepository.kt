package com.teamforce.thanksapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.entities.PushTokenEntity
import com.teamforce.thanksapp.utils.ResultWrapper
import com.teamforce.thanksapp.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsRepository @Inject constructor(
    private val thanksApi: ThanksApi
) {

    val state = MutableLiveData<NotificationStates>()

    suspend fun updatePushToken(token: PushTokenEntity): ResultWrapper<PushTokenEntity> {
        Log.d(PushNotificationService.TAG, "updatePushToken: ${token.token} ")
        return safeApiCall(Dispatchers.IO) {
            thanksApi.setPushToken(token)
        }
    }
}

sealed class NotificationStates {
    object Cleared : NotificationStates()
    object NotificationReceived : NotificationStates()
    data class PushTokenUpdated(val token: String, val deviceId: String) : NotificationStates()
}