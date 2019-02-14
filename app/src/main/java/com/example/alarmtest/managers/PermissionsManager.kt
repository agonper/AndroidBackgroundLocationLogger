package com.example.alarmtest.managers

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionsManager(private val activity: Activity) {

    private val locationReqCode = 0

    fun askLocationAccess() {
        val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        if (isGranted(locationPermission)) return
        ActivityCompat.requestPermissions(activity, arrayOf(locationPermission), locationReqCode)
    }

    private fun isGranted(permission: String): Boolean {
        if (isGrantedByManifest) return true
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

    private val isGrantedByManifest = Build.VERSION.SDK_INT < Build.VERSION_CODES.M
}