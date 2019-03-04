package com.example.alarmtest.alarms

import android.app.AlarmManager
import android.content.Context
import android.util.Log

class AlarmWatchdog(context: Context): AbstractAlarm(context, AlarmWatchdogReceiver::class.java) {
    private val timeInterval: Long = 15 * 60 * 1000

    override fun reschedule() {
        val alarmType = AlarmManager.RTC_WAKEUP

        val triggerAtMillis = System.currentTimeMillis() + timeInterval

        alarmManager.setRepeating(
            alarmType,
            triggerAtMillis,
            timeInterval,
            receiverPendingIntent
        )

        Log.d(javaClass.canonicalName, "Watchdog scheduled!")
    }
}