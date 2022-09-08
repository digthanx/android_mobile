package com.teamforce.thanksapp

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSharedViewModel @Inject constructor() : ViewModel() {
    init {
        checkNotifications()
    }

    private var counter = 0
    val state: MediatorLiveData<Int> = MediatorLiveData<Int>().apply {
        addSource(NotificationsRepository.state) {
            if (it is NotificationStates.NotificationReceived) {
                counter++
                value = counter
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