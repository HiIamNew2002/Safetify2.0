package com.example.safetify203v

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    private val TAG = "GeofenceBroadcastReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        val notificationHelper = NotificationHelper(context)
        val routesFragment = RoutesFragment() // Instantiate RoutesFragment
        Toast.makeText(context, "You entered an area", Toast.LENGTH_SHORT).show()
        notificationHelper.sendHighPriorityNotification("Beware!!", "You entered a dangerous area, check for more details", RoutesFragment::class.java)

    }
}