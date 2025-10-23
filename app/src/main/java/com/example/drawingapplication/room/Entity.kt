package com.example.drawingapplication.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.drawingapplication.Model.Stroke

@Entity(tableName = "Drawings")
data class DrawingEntity(
    val strokes: ArrayList<Stroke> = ArrayList<Stroke>(arrayListOf()),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)