package com.example.alarmtest.coordinates

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.*
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

@SuppressLint("MissingPermission")
class CoordinatesProvider(context: Context) {

    private val locationClient = LocationServices.getFusedLocationProviderClient(context) as FusedLocationProviderClient

    val coords: Observable<Coordinates> by lazy {
        Observable.create { emitter: ObservableEmitter<Coordinates>->
            val callback = createLocationCallback(emitter)
            locationClient.requestLocationUpdates(locationRequest, callback, null)
            emitter.setCancellable { locationClient.removeLocationUpdates(callback) }
        }
    }

    private fun createLocationCallback(emitter: ObservableEmitter<Coordinates>): LocationCallback {
        return object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                val coordinates = Coordinates.from(locationResult.lastLocation)
                emitter.onNext(coordinates)
            }
        }
    }

    private val locationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 100
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
}