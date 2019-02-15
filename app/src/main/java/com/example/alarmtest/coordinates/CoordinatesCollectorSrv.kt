package com.example.alarmtest.coordinates

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class CoordinatesCollectorSrv: IntentService("CoordinatesCollectorSrv") {

    private val tag = javaClass.canonicalName

    private val maxCoordsToCollect = 3L
    private val maxTimeBetweenCoords = 10L


    private lateinit var coordsProvider: CoordinatesProvider
    private lateinit var coordsRepository: CoordinatesRepository


    override fun onCreate() {
        super.onCreate()

        val coordsCollectNotification = CoordsCollectorNotification(this)

        coordsProvider = CoordinatesProvider(this)
        coordsRepository = CoordinatesRepository(this)

        startForeground(
            coordsCollectNotification.notificationId,
            coordsCollectNotification.create()
        )

        Log.d(tag, "Service created!")
    }

    override fun onHandleIntent(intent: Intent?) {
        try {
            collectCoordinates()
        } catch (e: Exception) {
            Log.e(tag, "Error: ${e.message}")
        }
    }

    override fun onDestroy() {
        val removingNotification = true
        stopForeground(removingNotification)

        Log.d(tag, "Service destroyed!")
        super.onDestroy()
    }

    private fun collectCoordinates() {
        val globalTimeout = maxCoordsToCollect * maxTimeBetweenCoords + 1L

        Log.d(tag, "Collecting coords...")
        val completed = acquireCoords()
            .doOnNext { coordinates ->
                Log.d(tag, coordinates.toString())
            }
            .toList()
            .map { coordinates ->
                coordinates.minBy { it.accuracy }
            }
            .flatMapCompletable { coordinates ->
                coordsRepository.save(coordinates)
            }
            .blockingAwait(globalTimeout, TimeUnit.SECONDS)
        if (completed) Log.d(tag, "Coordinates collected")
        else Log.d(tag, "Didn't store coordinates!")
    }

    private fun acquireCoords(): Observable<Coordinates> {
        return coordsProvider.coords
            .subscribeOn(AndroidSchedulers.mainThread()) // Get GPS coordinates in main thread
            .take(maxCoordsToCollect)
            .timeout(maxTimeBetweenCoords, TimeUnit.SECONDS,
                Schedulers.single(), Observable.empty()) // GPS can be sleeping
    }

    companion object {
        fun collect(context: Context) {
            Intent(
                context,
                CoordinatesCollectorSrv::class.java
            ).let { intent ->
                ContextCompat.startForegroundService(context, intent)
            }
        }
    }
}