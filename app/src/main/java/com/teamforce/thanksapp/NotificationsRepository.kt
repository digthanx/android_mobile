package com.teamforce.thanksapp

import androidx.lifecycle.MutableLiveData

object NotificationsRepository {

    val state = MutableLiveData<NotificationStates>()
}

sealed class NotificationStates {
    object Cleared : NotificationStates()
    object NotificationReceived : NotificationStates()
}