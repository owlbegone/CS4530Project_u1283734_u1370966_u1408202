package com.example.drawingapplication

import android.app.Application
import androidx.room.Room
import com.example.drawingapplication.room.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class CourseListApp: Application() {
    val scope =CoroutineScope(SupervisorJob())
    val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "myDB"
        ).build()
    }

    val repository by lazy { Repository(scope, db.drawingDao()) }

}