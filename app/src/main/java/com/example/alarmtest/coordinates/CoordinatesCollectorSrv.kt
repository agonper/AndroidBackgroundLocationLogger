package com.example.alarmtest.coordinates

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.alarmtest.managers.WakeManager
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class CoordinatesCollectorSrv: Service() {

    private val tag = javaClass.canonicalName

    private val maxCoordsToCollect = 3L
    private val maxTimeBetweenCoords = 10L
    private val globalTimeout = maxCoordsToCollect * maxTimeBetweenCoords + 1

    private lateinit var wakeLock: PowerManager.WakeLock

    private lateinit var coordsProvider: CoordinatesProvider
    private lateinit var coordsRepository: CoordinatesRepository

    private lateinit var coordsCollectorLooper: Looper
    private lateinit var coordsCollectorHandler: Handler

    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "Service created!")

        wakeLock = WakeManager(this).partialWakeLock()
        wakeLock.acquire(globalTimeout * 1100)

        val coordsCollectNotification = CoordsCollectorNotification(this)

        startForeground(
            coordsCollectNotification.notificationId,
            coordsCollectNotification.create()
        )

        coordsProvider = CoordinatesProvider(this)
        coordsRepository = CoordinatesRepository(this)

        val thread = HandlerThread(
            "CoordinatesCollectorSrvHT",
            Process.THREAD_PRIORITY_BACKGROUND
        )
        thread.start()

        coordsCollectorLooper = thread.looper
        coordsCollectorHandler = createCoordsCollectorHandler()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(tag, "Service started!")

        if (startId == 1) { // Allow only one coordinates collection task within a time window
            val message = coordsCollectorHandler.obtainMessage()
            message.arg1 = startId
            coordsCollectorHandler.sendMessage(message)
        } else {
            Log.d(tag, "Already collecting coordinates!")
            stopSelf(startId)
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // Service cannot be bound
    }

    override fun onDestroy() {
        val removingNotification = true
        stopForeground(removingNotification)
        wakeLock.release()
        Log.d(tag, "Service destroyed!")
        super.onDestroy()
    }

    private fun createCoordsCollectorHandler(): Handler = object : Handler(coordsCollectorLooper) {
        override fun handleMessage(msg: Message?) {
            msg ?: return
            Log.d(tag, "Collecting coords...")

            val workId = msg.arg1
            var timerDisposable: Disposable? = null

            val collectorDisposable = collectCoordinates().subscribe(
                {
                    Log.d(tag, "Coordinates collected")
                    finishWork(workId, timerDisposable)
                },
                { e->
                    Log.d(tag, "Didn't store coordinates!: ${e.message}")
                    finishWork(workId, timerDisposable)
                }
            )

            timerDisposable = Observable.timer(globalTimeout, TimeUnit.SECONDS)
                .subscribe {
                    Log.d(tag, "Timed out")
                    finishWork(workId, collectorDisposable)
                }
        }
    }

    private fun collectCoordinates(): Completable {
        return acquireCoords()
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
    }

    private fun acquireCoords(): Observable<Coordinates> {
        return coordsProvider.coords(coordsCollectorLooper)
            .take(maxCoordsToCollect)
            .timeout(maxTimeBetweenCoords, TimeUnit.SECONDS, Observable.empty()) // GPS can be in power-saving mode
    }

    private fun finishWork(workId: Int, complementaryTask: Disposable?) {
        complementaryTask?.apply {
            if (!isDisposed) dispose()
        }
        stopSelf(workId)
        Log.d(tag, "Finished work with id: $workId")
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