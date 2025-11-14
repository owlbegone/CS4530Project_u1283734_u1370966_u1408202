package com.example.drawingapplication

import android.app.Application
import androidx.room.Room
import com.example.drawingapplication.room.AppDatabase
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json

class DrawingApp: Application() {
    val scope =CoroutineScope(SupervisorJob())
    val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "myDB"
        ).build()
    }

    val client by lazy {HttpClient(Android)
    {
        install(ContentNegotiation){
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }}

    val repository by lazy { Repository(scope, db.drawingDao(), client)}

}