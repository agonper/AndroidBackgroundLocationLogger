package com.example.alarmtest.coordinates

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.alarmtest.MainActivity
import com.example.alarmtest.R

class CoordsCollectorNotification(private val context: Context) {
    private val displayMainActivityRequestCode = 0
    private val pendingIntentFlags = 0

    private val notificationChannelId = "COORDS_COLLECT_CHANNEL"
    private val notificationTitle = "Collecting coordinates"
    private val notificationDescription = "Warns you about passive data collection"
    private val notificationPriority = NotificationManagerCompat.IMPORTANCE_MIN

    private val mainActivityPendingIntent = Intent(
        context,
        MainActivity::class.java
    ).let { intent ->
        PendingIntent.getActivity(
            context,
            displayMainActivityRequestCode,
            intent,
            pendingIntentFlags
        )
    }

    val notificationId = Int.MAX_VALUE

    fun create(): Notification {
        return NotificationCompat.Builder(context, notificationChannelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(notificationTitle)
            .setPriority(notificationPriority)
            .setContentIntent(mainActivityPendingIntent)
            .build()
    }

    @SuppressLint("WrongConstant")
    fun setupChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            notificationChannelId,
            notificationTitle,
            notificationPriority)
        channel.description = notificationDescription

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}