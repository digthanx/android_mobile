package com.teamforce.thanksapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.entities.notifications.NotificationEntity
import com.teamforce.thanksapp.data.entities.notifications.PushTokenEntity
import com.teamforce.thanksapp.data.entities.notifications.RemovePushTokenEntity
import com.teamforce.thanksapp.data.entities.notifications.RemovePushTokenResultEntity
import com.teamforce.thanksapp.data.sources.notifications.NotificationsPagingSource
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.ResultWrapper
import com.teamforce.thanksapp.utils.UserDataRepository
import com.teamforce.thanksapp.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsRepository @Inject constructor(
    private val thanksApi: ThanksApi,
    private val userDataRepository: UserDataRepository
) {

    val state = MutableLiveData<NotificationStates>()

    suspend fun updatePushToken(token: PushTokenEntity): ResultWrapper<PushTokenEntity> {
        Log.d(PushNotificationService.TAG, "updatePushToken: ${token.token} ")
        return safeApiCall(Dispatchers.IO) {
            thanksApi.setPushToken(token)
        }
    }

    fun getNotifications(): Flow<PagingData<NotificationEntity>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = Consts.PAGE_SIZE,
                prefetchDistance = 1,
                pageSize = Consts.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                NotificationsPagingSource(
                    api = thanksApi
                )
            }
        ).flow
    }

    suspend fun deletePushToken(deviceId: String): ResultWrapper<RemovePushTokenResultEntity> {
        return safeApiCall(Dispatchers.IO) {
            val userId = userDataRepository.getProfileId()
            thanksApi.removePushToken(
                RemovePushTokenEntity(
                    deviceId, userId!!
                )
            )
        }
    }
}

sealed class NotificationStates {
    object Cleared : NotificationStates()
    object NotificationReceived : NotificationStates()
    data class PushTokenUpdated(val token: String, val deviceId: String) : NotificationStates()
}