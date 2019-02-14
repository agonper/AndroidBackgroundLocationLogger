package com.example.alarmtest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.alarmtest.coordinates.CoordinatesRepository

class MainViewModel(application: Application): AndroidViewModel(application) {
    private var coordinatesRepository = CoordinatesRepository(getApplication())

    val coordsCount: LiveData<Long> by lazy {
        coordinatesRepository.count
    }

    fun wipeCoords() {
        coordinatesRepository.deleteAll().subscribe()
    }
}