package com.example.drawingapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.test.InstrumentationRegistry
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.InputStream

@RunWith(AndroidJUnit4::class)
class APITesting {
    private lateinit var repository: Repository

    @Before
    fun setup() {
        val app = ApplicationProvider.getApplicationContext<DrawingApp>()
        repository = app.repository
    }

    @Test
    fun testAnalyzeDrawingLabel() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().context
        val assetManager = context.assets

        val inputStream = assetManager.open("TestImages/puppy.jpg")
        val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        val result = repository.analyzeDrawing(bitmap)


        assertTrue(result.labels.any { it.name.equals("Dog", ignoreCase = true) })
    }

    @Test
    fun testAnalyzeDrawingConfidence() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().context
        val assetManager = context.assets

        val inputStream = assetManager.open("TestImages/puppy.jpg")
        val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        val result = repository.analyzeDrawing(bitmap)
        assertTrue(result.labels.all { it.score in 0.0..1.0 })
    }

    @Test
    fun testAnalyzeDrawingReturnsPoly() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().context
        val assetManager = context.assets

        val inputStream = assetManager.open("TestImages/puppy.jpg")
        val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        val result = repository.analyzeDrawing(bitmap)

        //check that all labels have a bounding poly (They should according to our Response.kt class)
        assertTrue(result.labels.all { it.boundingPoly != null })
    }

    @Test
    fun testAnaylzeDrawingPolyHasCorrectVertices() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().context
        val assetManager = context.assets

        val inputStream = assetManager.open("TestImages/puppy.jpg")
        val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        val result = repository.analyzeDrawing(bitmap)

        //check that all the bounding polys have reasonable coordinates
        result.labels.forEach{obj ->
            obj.boundingPoly.normalizedVertices.forEach{ vertex ->
                assertTrue(vertex.x in 0.0..1.0)
                assertTrue(vertex.y in 0.0..1.0)
            }
        }
    }
}