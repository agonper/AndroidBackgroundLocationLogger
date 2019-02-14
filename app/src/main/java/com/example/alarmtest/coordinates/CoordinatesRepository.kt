package com.example.alarmtest.coordinates

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.alarmtest.persistence.AppDatabase
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

class CoordinatesRepository(context: Context) {

    private val coordinatesDao = AppDatabase.init(context).coordsDao()

    val count: LiveData<Long>
        get() = coordinatesDao.count()

    fun save(coordinates: Coordinates): Completable = coordinatesDao
        .save(coordinates)
        .subscribeOn(Schedulers.io())

    fun deleteAll(): Completable = Completable
        .fromRunnable {
            coordinatesDao.deleteAll()
        }
        .subscribeOn(Schedulers.io())
}