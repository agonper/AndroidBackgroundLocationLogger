package com.example.alarmtest.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.alarmtest.coordinates.Coordinates
import com.example.alarmtest.coordinates.CoordinatesDao

@Database(entities = [Coordinates::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun coordsDao(): CoordinatesDao

    companion object {
        private const val DB_NAME = "alarmtest-db"

        private lateinit var appDb: AppDatabase

        fun init(context: Context): AppDatabase {
            if (!::appDb.isInitialized) {
                appDb = Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME).build()
            }
            return appDb
        }
    }
}