package com.durranitech.salahsync.data.service

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.durranitech.salahsync.MainActivity
import com.durranitech.salahsync.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SalahSyncMessagingService : FirebaseMessagingService(){

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title?:return
        val body = message.notification?.body?:return
        val type = message.data["type"]
        val masjidId = message.data["masjidId"]

        showNotification(title,body, type,masjidId)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(title: String, body: String, type: String?, masjidId: String?) {
        val channelId =
            when(type){
                "ANNOUNCEMENT" -> NotificationChannels.ANNOUNCEMENTS
                "PRAYER_TIME_UPDATE" -> NotificationChannels.PRAYER_TIMES
                else -> NotificationChannels.GENERAL
            }

        // Pass metadata to MainActivity to handle deep linking/navigation on click
        val intent = Intent(this, MainActivity::class.java).apply {
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_type", type)
            putExtra("masjid_id", masjidId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,0,intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this,channelId)
            .setSmallIcon(R.drawable.main_icon)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationId = System.currentTimeMillis().toInt()
        NotificationManagerCompat.from(this).notify(notificationId,notification)
    }
}

object NotificationChannels{
    const val ANNOUNCEMENTS = "salahsync_notification"
    const val PRAYER_TIMES = "salahsync_prayer_times"
    const val GENERAL = "salahsync_general"
}
