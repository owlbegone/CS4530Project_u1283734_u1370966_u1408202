package com.example.drawingapplication.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Drawings")
data class DrawingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)