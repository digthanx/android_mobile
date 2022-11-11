package com.teamforce.thanksapp

import android.annotation.SuppressLint
import android.app.Application
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.SharedPreferences
import com.teamforce.thanksapp.data.entities.notifications.PushTokenEntity
import com.teamforce.thanksapp.utils.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("HardwareIds")
@HiltViewModel
class NotificationSharedViewModel @Inject constructor(
    private val notificationsRepository: NotificationsRepository,
    sharedPreferences: SharedPreferences,
    app: Application
) : AndroidViewModel(app) {
    init {
        if (sharedPreferences.pushToken != null) {
            val id = Settings.Secure.getString(
                app.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            updatePushToken(token = sharedPreferences.pushToken!!, deviceId = id)
        }
    }

    private var counter = 0
    val state: MediatorLiveData<Int> = MediatorLiveData<Int>().apply {
        addSource(notificationsRepository.state) {
            if (it is NotificationStates.NotificationReceived) {
                counter++
                value = counter
            }
            if (it is NotificationStates.PushTokenUpdated) {
                updatePushToken(token = it.token, deviceId = it.deviceId)
            }
        }
    }

    fun checkNotifications() {
        viewModelScope.launch {
            when (val result = notificationsRepository.getUnreadNotificationsAmount()) {
                is ResultWrapper.Success -> state.value = result.value.unreadNotificationsAmount
                else -> state.value = 0
            }
        }
    }

    fun dropNotificationCounter() {
        counter = 0
        state.value = counter
    }

    private fun updatePushToken(token: String, deviceId: String) {
        viewModelScope.launch {
            notificationsRepository.updatePushToken(
                PushTokenEntity(
                    device = deviceId,
                    token = token
                )
            )
        }
    }
}