package com.example.alarmtest.coordinates

import android.location.Location
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Coordinates(
    @PrimaryKey(autoGenerate = true) var uid: Long,
    var latitude: Double,
    var longitude: Double,
    var accuracy: Float,
    var timestamp: Long
) {
    companion object {
        fun from(location: Location) = Coordinates(0, location.latitude, location.longitude, location.accuracy, Date().time)
    }
}