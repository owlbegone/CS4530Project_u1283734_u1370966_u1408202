package com.example.drawingapplication

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.asAndroidBitmap
import com.example.drawingapplication.room.DrawingEntity
import com.example.drawingapplication.room.DrawingDao
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonArray
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class Repository(
    val scope: CoroutineScope,
    private val dao: DrawingDao,
    private val client: HttpClient
) {

    val allDrawings: Flow<List<DrawingEntity?>> = dao.getAllDrawings()

    fun bitmapToBase64(bitmap: Bitmap): String {
        val output = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
        val bytes = output.toByteArray()
        return android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
    }

    suspend fun analyzeDrawing(bitmap: Bitmap) {
        val url = "https://vision.googleapis.com/v1/images:annotate?key=${BuildConfig.API_KEY}"
        val base64Ver = bitmapToBase64(bitmap)

        val requestJson = """
            {
                "requests": [
                    {
                        "image": {
                            "content": "$base64Ver"
                            },
                            "features": [
                            {
                            "type": "LABEL_DETECTION",
                            "maxResults": 5
                            },
                            {
                             "type": "OBJECT_LOCALIZATION",
                             "maxResults": 10
                            }
                            ]
                        }
                    ]
                }
        """.trimIndent()
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(requestJson)
        }.body<JsonObject>()

        Log.e("response", response.toString())
    }

    fun addDrawing(drawingPath: String) {
        scope.launch {
            val drawingObj = DrawingEntity(drawingPath)
            dao.insertDrawing(drawingObj)
        }
    }

    fun newDrawing(drawing: DrawingEntity) {
        scope.launch {
            dao.insertDrawing(drawing)
        }
    }

    fun insertDrawing(drawing: DrawingEntity) {
        scope.launch {
            dao.insertDrawing(drawing)
        }
    }

    fun delDrawing(id: Int) {
        scope.launch {
            dao.deleteDrawingById(id)
        }
    }

    suspend fun getDrawingById(id: Int): String {
        return dao.getDrawingById(id)
    }

    suspend fun insertAndReturnId(drawing: DrawingEntity): Long {
        return dao.insertDrawingAndReturnId(drawing)
    }
}