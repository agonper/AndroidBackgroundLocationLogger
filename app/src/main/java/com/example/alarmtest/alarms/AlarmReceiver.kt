package com.example.alarmtest.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.alarmtest.coordinates.CoordinatesCollectorSrv

class AlarmReceiver: BroadcastReceiver() {

    private val tag = javaClass.canonicalName

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(tag, "Alarm triggered")
        context?.let { ctx ->
            OneMinuteAlarm(ctx).reschedule()

            CoordinatesCollectorSrv.collect(ctx)

            Log.d(tag, "Work enqueued")
        }
    }
}