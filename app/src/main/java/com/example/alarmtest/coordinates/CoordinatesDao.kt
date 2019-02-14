package com.example.alarmtest.coordinates

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Completable

@Dao
interface CoordinatesDao {
    @Query("SELECT COUNT(*) FROM coordinates")
    fun count(): LiveData<Long>

    @Insert
    fun save(coords: Coordinates): Completable

    @Query("DELETE FROM coordinates")
    fun deleteAll()
}