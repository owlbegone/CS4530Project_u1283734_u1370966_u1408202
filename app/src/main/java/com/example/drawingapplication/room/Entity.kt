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

class Converters {
    @TypeConverter
    fun drawingToJSON(strokeList: ArrayList<Stroke>?): String? {
        val strokeJSON = JSONArray()
        if (strokeList != null) {
            for(stroke in strokeList)
            {
                val thisJSON = JSONObject()
                val thisJSONOffset = JSONArray()
                for(offset in stroke.lines)
                {
                    val thisOffset = JSONObject()
                    thisOffset.put("x", offset.x)
                    thisOffset.put("y", offset.y)
                    thisJSONOffset.put(thisOffset)
                }
                thisJSON.put("lines", thisJSONOffset)
                thisJSON.put("color", JSONObject(stroke.color.toString()))
                thisJSON.put("size", JSONObject(stroke.size.toString()))
                thisJSON.put("type", JSONObject(stroke.type))
                strokeJSON.put(thisJSON)
            }
        }
        return strokeJSON.toString()
    }

    @TypeConverter
    fun JSONToDrawing(thisJSON: String?): ArrayList<Stroke>?
    {
        if (thisJSON == null)
        {
            return null
        }
        val strokeList = ArrayList<Stroke>()
        val thisArray = JSONArray(thisJSON)
        for (entry in 0 until thisArray.length())
        {
            val thisStroke = thisArray.getJSONObject(entry)
            val newLines = ArrayList<Offset>()

            val strokeColor = Color(thisStroke.getString("color").toULong())
            val strokeSize = thisStroke.getInt("size")
            val strokeType = thisStroke.getString("type")
            val strokeLines = ArrayList<Offset>()

            val lineList = thisStroke.getJSONArray("lines")
            for (line in 0 until lineList.length())
            {
                val thisOffset = lineList.getJSONObject(line)
                val x = thisOffset.getInt("x")
                val y = thisOffset.getInt("y")
                strokeLines.add(Offset(x.toFloat(),y.toFloat()))
            }
            val newStroke = Stroke(
                lines = strokeLines,
                color = strokeColor,
                size = strokeSize,
                type = strokeType
            )
            strokeList.add(newStroke)
        }
        return strokeList
    }
}

@Entity(tableName = "Drawings")
data class DrawingEntity(
    val strokes: ArrayList<Stroke> = ArrayList<Stroke>(arrayListOf()),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)