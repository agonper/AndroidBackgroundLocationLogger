package com.example.alarmtest.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class AlarmWatchdogReceiver: BroadcastReceiver() {

    private val tag = javaClass.canonicalName

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(tag, "Watchdog triggered")
        context?.let {ctx ->
            val alarm = OneMinuteAlarm(ctx)
            if (!alarm.isUp) {
                alarm.schedule()
            }
        }
    }
}