package com.example.drawingapplication

import java.io.IOException;
import androidx.room.Room
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.drawingapplication.room.AppDatabase
import com.example.drawingapplication.room.DrawingDao
import com.example.drawingapplication.room.DrawingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTesting {
    private lateinit var database: AppDatabase
    private lateinit var drawingDao: DrawingDao

    @Before
    fun createDb(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        drawingDao = database.drawingDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertDrawingTest() = runBlocking {
        val drawing = DrawingEntity("test")
        drawingDao.insertDrawing(drawing)
        val byId = drawingDao.getDrawingById(1)
        assert(byId == "test")
    }

    @Test
    @Throws(Exception::class)
    fun insertDrawingAndReturnIdTest() = runBlocking {
            val drawing = DrawingEntity("test")
            val id = drawingDao.insertDrawingAndReturnId(drawing)
            val path = drawingDao.getDrawingById(id.toInt())
            assert(path == "test")
        }

    @Test
    @Throws(Exception::class)
    fun deleteDrawingByIdTest() = runBlocking {
        val drawing = DrawingEntity("test")
        drawingDao.insertDrawing(drawing)
        drawingDao.deleteDrawingById(1)
        val returnedDrawing = drawingDao.getDrawingById(1)
        assert(returnedDrawing == null)
    }

    @Test
    @Throws(Exception::class)
    fun getAllDrawingsTest() = runBlocking {
        val drawing1 = DrawingEntity("test1")
        val drawing2 = DrawingEntity("test2")
        drawingDao.insertDrawing(drawing1)
        drawingDao.insertDrawing(drawing2)
        val allDrawings = drawingDao.getAllDrawings().first()
        assert(allDrawings.size == 2)
    }
}