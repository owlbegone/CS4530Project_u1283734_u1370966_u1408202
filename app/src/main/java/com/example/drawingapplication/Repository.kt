package com.example.drawingapplication


import com.example.drawingapplication.room.DrawingEntity
import com.example.drawingapplication.room.DrawingDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class Repository (val scope: CoroutineScope, private val dao: DrawingDao) {

    val allCourses: Flow<List<DrawingEntity?>> = dao.getAllCourses()

    fun addCourse(department: String, courseNum: String, loc: String) {
        scope.launch {
            delay(1000) // simulates network delay
            val drawingObj = DrawingEntity()
            dao.insertDrawing(drawingObj)
        }
    }

    fun delCourse(id: Int) {
        scope.launch {
            delay(1000) // simulates network delay
            dao.deleteDrawingById(id)
        }
    }
}