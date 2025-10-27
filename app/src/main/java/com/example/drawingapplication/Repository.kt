package com.example.drawingapplication

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.example.drawingapplication.room.DrawingEntity
import com.example.drawingapplication.room.DrawingDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class Repository (val scope: CoroutineScope, private val dao: DrawingDao) {

    val allDrawings: Flow<List<DrawingEntity?>> = dao.getAllDrawings()

    fun addDrawing(drawingPath: String) {
        scope.launch {
            delay(1000) // simulates network delay
            val drawingObj = DrawingEntity(drawingPath)
            dao.insertDrawing(drawingObj)
        }
    }

    fun newDrawing(drawing: DrawingEntity) {
        scope.launch {
            delay(1000)
            dao.insertDrawing(drawing)
        }
    }

    fun insertDrawing(drawing: DrawingEntity) {
        scope.launch {
            delay(1000) // simulates network delay
            dao.insertDrawing(drawing)
        }
    }

    fun delDrawing(id: Int) {
        scope.launch {
            delay(1000) // simulates network delay
            dao.deleteDrawingById(id)
        }
    }

    suspend fun getDrawingById(id: Int): String {
            delay(1000) // simulates network delay
            return dao.getDrawingById(id)
    }

    suspend fun insertAndReturnId(drawing: DrawingEntity): Long {
        return dao.insertDrawingAndReturnId(drawing)
    }
}