package com.example.alarmtest.alarms

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.util.Log

class OneMinuteAlarm(context: Context): AbstractAlarm(context, AlarmReceiver::class.java) {
    private val timeInterval = 60 * 1000

    override fun reschedule() {
        val alarmType = AlarmManager.RTC_WAKEUP

        val triggerAtMillis = System.currentTimeMillis() + timeInterval

        when {
            Build.VERSION.SDK_INT >= 23 -> alarmManager.setExactAndAllowWhileIdle(
                alarmType,
                triggerAtMillis,
                receiverPendingIntent
            )
            Build.VERSION.SDK_INT >= 19 -> alarmManager.setExact(
                alarmType,
                triggerAtMillis,
                receiverPendingIntent
            )
            else -> alarmManager.set(
                alarmType,
                triggerAtMillis,
                receiverPendingIntent
            )
        }

        Log.d(javaClass.canonicalName, "Alarm scheduled!")
    }
}