package com.example.drawingapplication.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.drawingapplication.Model.Stroke
import androidx.room.TypeConverter
import org.json.JSONObject
import org.json.JSONArray
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import org.json.JSONException
import java.util.*

@Entity(tableName = "Drawings")
data class DrawingEntity(
    var drawingPath: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)