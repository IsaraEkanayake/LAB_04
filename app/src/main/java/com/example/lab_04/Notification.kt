package com.example.lab_04

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

// Constants for notification channel, ID, and extras
const val channelID = "channel1"
const val notificationID = 1
const val titleExtra = "titleextra"
const val messageExtra = "messageextra"

class Notification : BroadcastReceiver() {

    // This method is triggered when the scheduled notification is received
    override fun onReceive(context: Context, intent: Intent) {
        // Build the notification with the received title and message
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Icon for the notification
            .setContentTitle(intent.getStringExtra(titleExtra)) // Get the title from the intent
            .setContentText(intent.getStringExtra(messageExtra)) // Get the message from the intent
            .build()

        // Get the NotificationManager service to trigger the notification
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification) // Send the notification using the manager
    }
}
