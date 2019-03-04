package com.example.alarmtest.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

abstract class AbstractAlarm(private val context: Context, receiver: Class<out Any>) {

    protected val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private val receiverIntent = Intent(context, receiver)
    protected val receiverPendingIntent: PendingIntent =
        PendingIntent.getBroadcast(context, 0, receiverIntent, 0)

    fun schedule() {
        alarmManager.cancel(receiverPendingIntent)
        reschedule()
    }

    abstract fun reschedule()

    val isUp: Boolean
        get() = PendingIntent.getBroadcast(
            context, 0,
            receiverIntent,
            PendingIntent.FLAG_NO_CREATE
        ) != null
}