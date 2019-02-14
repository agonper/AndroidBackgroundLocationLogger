package com.example.alarmtest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.alarmtest.coordinates.Coordinates
import com.example.alarmtest.coordinates.CoordinatesRepository

class MainViewModel(application: Application): AndroidViewModel(application) {
    private var coordinatesRepository = CoordinatesRepository(getApplication())

    val coordsCount: LiveData<Long> by lazy {
        coordinatesRepository.count
    }

    fun store(coords: Coordinates) {
        coordinatesRepository.save(coords).subscribe()
    }

    fun wipeCoords() {
        coordinatesRepository.deleteAll().subscribe()
    }
}