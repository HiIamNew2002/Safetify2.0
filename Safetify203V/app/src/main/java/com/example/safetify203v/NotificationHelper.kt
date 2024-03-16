package com.example.safetify203v

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

private const val TAG = "NotificationHelperXXX"

class NotificationHelper(private val context : Context) : ContextWrapper(context) {
    

    private val CHANNEL_NAME = "APP_DEFAULT_CHANNEL"
    private val CHANNEL_ID = "com.example.notifications$CHANNEL_NAME"

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannels() {
        try {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.description = "This is the description of the channel."
            notificationChannel.lightColor = Color.RED
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            manager.createNotificationChannel(notificationChannel)

            Log.d(TAG, "createChannels: Notification channel has created")
        } catch (e: Exception) {
            Log.d(TAG, "Error creating notification channel: ${e.message}")
        }
    }


    fun sendHighPriorityNotification(title: String, body: String, activityName: Class<*>) {

        Log.d(TAG, "sendHighPriorityNotification: Triggered for notification")
        
        val intent = Intent(this, activityName)
        val pendingIntent = PendingIntent.getActivity(
            this,
            267,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle().setSummaryText("summary").setBigContentTitle(title).bigText(body))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context, "Notification permission is required", Toast.LENGTH_SHORT).show()
            return
        }
        
        
        NotificationManagerCompat.from(this).notify(Random().nextInt(), notification)
    }
}

