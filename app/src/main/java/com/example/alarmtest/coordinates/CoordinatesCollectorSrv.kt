package com.example.alarmtest.coordinates

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class CoordinatesCollectorSrv: IntentService("CoordinatesCollectorSrv") {

    private val tag = javaClass.canonicalName

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
        super.onDestroy()
    }

    private fun collectCoordinates() {
        Log.d(tag, "Collecting coords...")
        val completed = obtainCoordinates(3)
            .timeout(
                15,
                TimeUnit.SECONDS,
                obtainCoordinates(1).timeout(5, TimeUnit.SECONDS)
            )
            .doOnNext {coordinates ->
                Log.d(tag, coordinates.toString())
            }
            .toList()
            .map { coordinates ->
                coordinates.minBy { it.accuracy }
            }
            .flatMapCompletable { coordinates ->
                coordsRepository.save(coordinates)
            }
            .blockingAwait(15, TimeUnit.SECONDS)
        if (completed) Log.d(tag, "Coordinates collected")
        else Log.d(tag, "Didn't store coordinates!")
    }

    private fun obtainCoordinates(amount: Long): Observable<Coordinates> = coordsProvider
        .coords
        .subscribeOn(AndroidSchedulers.mainThread())
        .take(amount)

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