package com.example.drawingapplication.screens

import android.app.Application
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.drawingapplication.DrawingApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.collections.plus
import com.example.drawingapplication.Model.Stroke
import com.example.drawingapplication.room.DrawingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MyViewModel(application: Application) : AndroidViewModel(application) {
    // Variables for the lists containing all the strokes on the canvas
//    private val strokesMutable = MutableStateFlow<ArrayList<Stroke>>(arrayListOf())
//    val strokesReadOnly: MutableStateFlow<ArrayList<Stroke>> = strokesMutable


    val dao = (application as DrawingApp).repository

    // Variables for the current stroke (if one is being drawn)
    private val currentStrokeMutable = MutableStateFlow(listOf<Offset>())
    val currentStrokeReadOnly: MutableStateFlow<List<Offset>> = currentStrokeMutable

    // Variables for the current color being used
    private val currentColorMutable = MutableStateFlow(Color.Black)

    val currentColorReadOnly: MutableStateFlow<Color> = currentColorMutable

    // Variables for the current size being used
    private val currentSizeMutable = MutableStateFlow(4)

    val currentSizeReadOnly: MutableStateFlow<Int> = currentSizeMutable

    // Variables for the current pen shape being used
    private val currentShapeMutable = MutableStateFlow("Square")

    val currentShapeReadOnly: MutableStateFlow<String> = currentShapeMutable

    //get the current drawing
    fun getDrawingById(drawingId: Int): Flow<DrawingEntity?> {
        return dao.getDrawingById(drawingId)
    }

    // Adds a stroke to the list once the user lifts their finger
    fun addStroke(drawingId: Int, newStroke: Stroke) {
        viewModelScope.launch {
            // collect the latest drawing value from the flow
            val drawing = dao.getDrawingById(drawingId).firstOrNull()

            if (drawing != null) {
                val updatedStrokes = drawing.strokes.toMutableList().apply { add(newStroke) }
                val updatedDrawing = drawing.copy(strokes = ArrayList(updatedStrokes))
                dao.updateDrawing(updatedDrawing)
            }
        }
    }

    fun getStrokes(drawingId: Int): Flow<List<Stroke>> {
        return dao.getDrawingById(drawingId)
            .map { drawing -> drawing?.strokes ?: emptyList() }
    }
    // Adds a point to a list of the current stroke
    fun addPoint(point: Offset) {
        currentStrokeMutable.value = currentStrokeMutable.value + point
    }

    // Changes the color
    fun changeColor(color: Color) {
        currentColorMutable.value = color
    }

    // Changes the size
    fun changeSize(size: Float) {
        currentSizeMutable.value = size.toInt()
    }

    // Changes the shape
    fun changeShape(type: String) {
        currentShapeMutable.value = type
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasScreen(navController: NavHostController, drawingId: Int) {
    val myVM: MyViewModel = viewModel()

    val strokes by myVM.getStrokes(drawingId).collectAsState(initial = emptyList())

    // observe current stroke, color, size, shape
    val observableCurrentStroke by myVM.currentStrokeReadOnly.collectAsState()
    val observableColor by myVM.currentColorReadOnly.collectAsState()
    val strokeSize by myVM.currentSizeReadOnly.collectAsState()
    val strokeShape by myVM.currentShapeReadOnly.collectAsState()

    var sliderPosition by remember { mutableFloatStateOf(strokeSize.toFloat()) }
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

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
                        myVM.addStroke(drawingId, Stroke(observableCurrentStroke, observableColor, strokeSize))
                    }
                )
            }

    ) {
        // Draw finished strokes
        for (stroke in strokes) {
            for (i in 0 until stroke.lines.size - 1) {
                // currently there's only square and round; the else is when the type is round.
                // if we add more shapes this should be changed.
                if(stroke.type == "Square")
                {
                    drawLine(
                        color = stroke.color,
                        start = stroke.lines[i],
                        end = stroke.lines[i + 1],
                        strokeWidth = stroke.size.dp.toPx()
                    )
                }
                else
                {
                    drawLine(
                        color = stroke.color,
                        start = stroke.lines[i],
                        end = stroke.lines[i + 1],
                        cap = StrokeCap.Round,
                        strokeWidth = stroke.size.dp.toPx()
                    )
                }
            }
        }

        // Draw stroke in progress
        for (i in 0 until observableCurrentStroke.size - 1) {
            if(strokeShape == "Square")
            {
                drawLine(
                    color = observableColor,
                    start = observableCurrentStroke[i],
                    end = observableCurrentStroke[i + 1],
                    strokeWidth = strokeSize.dp.toPx()
                )
            }
            else
            {
                drawLine(
                    color = observableColor,
                    start = observableCurrentStroke[i],
                    end = observableCurrentStroke[i + 1],
                    cap = StrokeCap.Round,
                    strokeWidth = strokeSize.dp.toPx()
                )
            }
        }
    }

    Column (modifier = Modifier
        .fillMaxSize()
        .padding(50.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start){
        Row{
            // Buttons for changing pen color
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
        Row(verticalAlignment = Alignment.CenterVertically){
            // This displays the size slider
            Text(text = "Brush size: " + sliderPosition.toInt().toString(), fontSize = 13.sp,
                modifier = Modifier
                    .padding(end = 20.dp))
            Slider(
                value = sliderPosition,
                onValueChange = {sliderPosition = it
                                myVM.changeSize(it)},
                steps = 30,
                valueRange = 0f..31f,
                thumb = {
                    SliderDefaults.Thumb(
                        interactionSource = interactionSource,
                        thumbSize = DpSize(20.dp, 20.dp)
                    )
                }
            )
        }
        Row (verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start){
            // This displays the menu for the brush shape
            Text(text = "Brush shape: $strokeShape", fontSize = 13.sp)
            IconButton(onClick = { expanded = !expanded }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Shape Options")
            }
            DropdownMenu(expanded = expanded,
                onDismissRequest = {expanded = false}) {
                DropdownMenuItem(
                    text = { Text("Square")},
                    onClick = {myVM.changeShape("Square")}
                )
                DropdownMenuItem(
                    text = {Text("Round")},
                    onClick = {myVM.changeShape("Round")}
                )
            }
        }

        Row (verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start){
            // This displays the menu for the brush shape
            Text(text = "Current Color: " + observableColor.red + " " +
                                            observableColor.blue + " " +
                                            observableColor.green , fontSize = 13.sp)


        }
    }
}