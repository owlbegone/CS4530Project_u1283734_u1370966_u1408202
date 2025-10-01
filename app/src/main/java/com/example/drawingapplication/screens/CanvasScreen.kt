package com.example.drawingapplication.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.collections.plus

class MyViewModel : ViewModel() {
    private val strokesMutable = MutableStateFlow(listOf<List<Offset>>())
    val strokesReadOnly: MutableStateFlow<List<List<Offset>>> = strokesMutable

    //We will need to have a map that connects the strokes to their color here to stop it from recoloring
    //all strokes with the current color. This is TBD

    private val currentStrokeMutable = MutableStateFlow(listOf<Offset>())
    val currentStrokeReadOnly: MutableStateFlow<List<Offset>> = currentStrokeMutable

    private val currentColorMutable = MutableStateFlow(Color.Black)

    val currentColorReadOnly: MutableStateFlow<Color> = currentColorMutable

    fun addStroke(stroke: List<Offset>) {
        strokesMutable.value = strokesMutable.value + listOf(stroke)
        currentStrokeMutable.value = emptyList() // reset current stroke
    }

    fun addPoint(point: Offset) {
        currentStrokeMutable.value = currentStrokeMutable.value + point
    }

    fun changeColor(color: Color) {
        currentColorMutable.value = color
    }
}
@Composable
fun CanvasScreen(navController: NavHostController, myVM: MyViewModel = viewModel()) {
    val observableStrokes by myVM.strokesReadOnly.collectAsState()
    val observableCurrentStroke by myVM.currentStrokeReadOnly.collectAsState()
    val observableColor by myVM.currentColorReadOnly.collectAsState()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        myVM.addPoint(offset)
                    },
                    onDrag = { change, x ->
                        change.consume()
                        myVM.addPoint(change.position)
                    },
                    onDragEnd = {
                        myVM.addStroke(observableCurrentStroke)
                    }
                )
            }

    ) {
        // Draw finished strokes
        for (stroke in observableStrokes) {
            for (i in 0 until stroke.size - 1) {
                drawLine(
                    color = observableColor,
                    start = stroke[i],
                    end = stroke[i + 1],
                    strokeWidth = 4f
                )
            }
        }

        // Draw stroke in progress
        for (i in 0 until observableCurrentStroke.size - 1) {
            drawLine(
                color = observableColor,
                start = observableCurrentStroke[i],
                end = observableCurrentStroke[i + 1],
                strokeWidth = 4f
            )
        }
    }

    Column (modifier = Modifier.fillMaxSize().padding(50.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally){
        Row{
            Button(onClick = { myVM.changeColor(Color.Red) }) {
                Text("Red")
            }
            Button(onClick = { myVM.changeColor(Color.Blue) }) {
                Text("Blue")
            }
            Button(onClick = { myVM.changeColor(Color.Green) }) {
                Text("Green")
            }
        }

    }
}