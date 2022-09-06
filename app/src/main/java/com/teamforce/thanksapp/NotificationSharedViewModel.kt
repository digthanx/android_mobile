package com.teamforce.thanksapp

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationSharedViewModel @Inject constructor() : ViewModel() {

    var counter = 0
    val state: MediatorLiveData<Int> = MediatorLiveData<Int>().apply {
        addSource(NotificationsRepository.state) {
            if (it is NotificationStates.NotificationReceived) {
                counter++
                value = counter
            }
        }
    }

}