package com.teamforce.thanksapp

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.SharedPreferences
import com.teamforce.thanksapp.data.entities.PushTokenEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("HardwareIds")
@HiltViewModel
class NotificationSharedViewModel @Inject constructor(
    private val notificationsRepository: NotificationsRepository,
    private val sharedPreferences: SharedPreferences,
    app: Application
) : AndroidViewModel(app) {
    init {
        Log.d(PushNotificationService.TAG, "init notificationsViewModel")
        if (sharedPreferences.pushToken != null) {
            val id = Settings.Secure.getString(
                app.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            viewModelScope.launch {

                notificationsRepository.updatePushToken(
                    PushTokenEntity(
                        device = id,
                        token = sharedPreferences.pushToken!!
                    )
                )
            }
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
                viewModelScope.launch {
                    notificationsRepository.updatePushToken(
                        PushTokenEntity(
                            device = it.deviceId,
                            token = it.token
                        )
                    )
                }
            }
        }
    }

    private fun checkNotifications() {
        viewModelScope.launch {
            delay(300)
            state.value = 0
        }
    }

    fun dropNotificationCounter() {
        counter = 0
        state.value = counter
    }

}