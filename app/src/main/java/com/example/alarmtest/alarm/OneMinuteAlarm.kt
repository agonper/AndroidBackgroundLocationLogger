package com.example.alarmtest.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class OneMinuteAlarm(context: Context) {
    private val timeInterval = 60 * 1000

    private val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val alarmIntent: PendingIntent

    init {
        alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }
    }

    fun schedule() {
        alarmManager.cancel(alarmIntent)
        reschedule()
    }

    fun reschedule() {
        val alarmType = AlarmManager.RTC_WAKEUP

        val triggerAtMillis = System.currentTimeMillis() + timeInterval

        when {
            Build.VERSION.SDK_INT >= 23 -> alarmManager.setExactAndAllowWhileIdle(
                alarmType,
                triggerAtMillis,
                alarmIntent
            )
            Build.VERSION.SDK_INT >= 19 -> alarmManager.setExact(
                alarmType,
                triggerAtMillis,
                alarmIntent
            )
            else -> alarmManager.set(
                alarmType,
                triggerAtMillis,
                alarmIntent
            )
        }

        Log.d(javaClass.canonicalName, "Alarm scheduled!")
    }
}