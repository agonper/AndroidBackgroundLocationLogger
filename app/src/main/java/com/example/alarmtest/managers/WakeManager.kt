package com.example.alarmtest.managers

import android.content.Context
import android.os.PowerManager

class WakeManager(context: Context) {
    private val wakeLogTag = "AlarmTest::CPUWakeLock"

    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    fun partialWakeLock(): PowerManager.WakeLock {
        return powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, wakeLogTag)
    }
}