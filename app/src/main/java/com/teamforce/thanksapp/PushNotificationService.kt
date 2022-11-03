package com.teamforce.thanksapp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.teamforce.thanksapp.data.SharedPreferences
import com.teamforce.thanksapp.presentation.activity.MainActivity
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class PushNotificationService : FirebaseMessagingService() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PushNotificationServiceEntryPoint {
        fun notificationsRepository(): NotificationsRepository
        fun sharedPreferences(): SharedPreferences


    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(TAG, "onMessageReceived: $message")
        if (message.notification != null) {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                PushNotificationServiceEntryPoint::class.java
            )
            entryPoint.notificationsRepository().state.postValue(NotificationStates.NotificationReceived)
            generateNotification(message.notification!!.title!!, message.notification!!.body!!)
        }
    }

    @SuppressLint("HardwareIds")
    override fun onNewToken(token: String) {
        Log.d(TAG, "onNewToken: $token")
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            PushNotificationServiceEntryPoint::class.java
        )

        //сохраняем токен после обновления или получения
        entryPoint.sharedPreferences().pushToken = token
        val deviceId = Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        //сообщаем слушателей о том, что токен обновился. Вдруг он обновится во время работы программы.
        //Это вряд ли, но на всякий случай
        entryPoint.notificationsRepository().state.postValue(
            NotificationStates.PushTokenUpdated(
                token,
                deviceId
            )
        )
    }

    private fun generateNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        // channel id, channel name

        var builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title, message))

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0, builder.build())


    }

    private fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteView = RemoteViews(BuildConfig.APPLICATION_ID, R.layout.sample_notification)

        remoteView.setTextViewText(R.id.notif_title, title)
        remoteView.setTextViewText(R.id.notif_description, message)
        remoteView.setImageViewResource(R.id.notif_image, R.drawable.ic_logo)

        return remoteView;
    }

    companion object {
        const val TAG = "PushNotificationService"
        const val channelId = "notification_channel"
        const val channelName = "com.teamforce.thanksapp"
    }
}