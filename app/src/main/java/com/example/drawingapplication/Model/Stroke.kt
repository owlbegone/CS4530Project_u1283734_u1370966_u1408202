package com.example.drawingapplication.Model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class Stroke(val lines : List<Offset> = emptyList(),
                  val color : Color = Color.Black,
                  val size : Int = 4,
                  val type : String = "Square")