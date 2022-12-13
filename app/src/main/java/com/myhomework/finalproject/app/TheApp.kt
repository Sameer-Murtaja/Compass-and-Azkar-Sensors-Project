package com.myhomework.finalproject.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class TheApp: Application() {
    companion object{
        const val CHANNEL_NAME = "flashlightServiceChannel"
    }

    override fun onCreate() {
        super.onCreate()
        makeNotificationChannel()
    }

    private fun makeNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val serviceChannel = NotificationChannel(
                CHANNEL_NAME,"flashlight Service Channel",
                NotificationManager.IMPORTANCE_HIGH)

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)

        }
    }
}