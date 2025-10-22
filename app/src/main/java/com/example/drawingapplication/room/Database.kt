package com.example.drawingapplication.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DrawingEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun drawingDao(): DrawingDao
}
