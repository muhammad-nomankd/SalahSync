package com.durranitech.salahsync

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.durranitech.salahsync.data.service.NotificationChannels
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SalahSyncApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(
                NotificationChannel(
                    NotificationChannels.ANNOUNCEMENTS,
                    "Masjid Announcements",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply { description = "Notifications for new masjid announcements" }
            )

            manager.createNotificationChannel(
                NotificationChannel(
                    NotificationChannels.PRAYER_TIMES,
                    "Prayer Time Updates",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply { description = "Notifications when prayer times are updated" }
            )
        }
    }
}