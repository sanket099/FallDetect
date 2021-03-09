package com.sanket.falldetect

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build

class ApplicationClass: Application() {
    val CHANNEL_ID = "ChannelId"
    override fun onCreate() {
        super.onCreate()

        //first thing which runs on app start
        createNotificationChannel()
        val intent = Intent(this, MyIntentService::class.java)
        MyIntentService.enqueueWork(this, intent)
    }

    //creating a channel
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Example Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }
}