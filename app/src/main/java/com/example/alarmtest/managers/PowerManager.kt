package com.example.alarmtest.managers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings

class PowerManager(private val context: Context) {

    private val packageName = "com.example.alarmtest"

    private val powerManager: PowerManager =  context.getSystemService(Context.POWER_SERVICE) as PowerManager

    @SuppressLint("BatteryLife")
    fun requestSavingsDeactivation() {
        if (Build.VERSION.SDK_INT < 23) return
        if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
            Intent(
                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                Uri.parse("package:$packageName")
            ).let { intent ->
                context.startActivity(intent)
            }
        }
    }
}