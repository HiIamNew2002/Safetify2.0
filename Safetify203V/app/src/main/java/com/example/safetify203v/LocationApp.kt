package com.example.safetify203v

import android.app.Application
import android.os.Build

class LocationApp: Application() {

    override fun onCreate() {
        super.onCreate()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotification = NotificationHelper(applicationContext)
            mNotification.createChannels()
        }
    }
}