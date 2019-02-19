package com.example.alarmtest.coordinates

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import io.reactivex.*

@SuppressLint("MissingPermission")
class CoordinatesProvider(context: Context) {

    private val locationClient = LocationServices.getFusedLocationProviderClient(context) as FusedLocationProviderClient

    fun coords(looper: Looper): Observable<Coordinates> {
        return Observable.create { emitter: ObservableEmitter<Coordinates> ->
            val callback = createLocationCallback(emitter)
            locationClient.requestLocationUpdates(locationRequest, callback, looper)
            emitter.setCancellable { locationClient.removeLocationUpdates(callback) }
        }
    }

    val lastKnownCoords: Maybe<Coordinates>
        get() {
            return Maybe.create { emitter: MaybeEmitter<Coordinates> ->
                locationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        emitter.onSuccess(Coordinates.from(location))
                        return@addOnSuccessListener
                    }
                    emitter.onComplete()
                }.addOnFailureListener { err ->
                    emitter.onError(err)
                }
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