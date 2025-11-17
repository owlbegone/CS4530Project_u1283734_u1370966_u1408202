package com.example.drawingapplication.screens

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Picture
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
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
import kotlinx.coroutines.flow.map
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
//import com.example.drawingapplication.BuildConfig
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class CanvasViewModel(application: Application) : AndroidViewModel(application) {
    // Variables for the lists containing all the strokes on the canvas
    private val strokesMutable = MutableStateFlow<ArrayList<Stroke>>(arrayListOf())
    val strokesReadOnly: MutableStateFlow<ArrayList<Stroke>> = strokesMutable


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

    // Variables for the current bitmap (stores all of the existing information about Canvas)
    private val bitmapMutable = MutableStateFlow(createBitmap(1,1))
    val bitmapReadOnly: MutableStateFlow<Bitmap> = bitmapMutable

    private val isNewDrawingMutable = MutableStateFlow(true)
    val isNewDrawingReadOnly: MutableStateFlow<Boolean> = isNewDrawingMutable

    private val hasBeenSavedMutable = MutableStateFlow(false)
    val hasBeenSavedReadOnly: MutableStateFlow<Boolean> = hasBeenSavedMutable


    //get the current drawing to be drawn
    suspend fun updateCanvas(drawingId: Int) {
        viewModelScope.launch{
            val drawingPath = dao.getDrawingById(drawingId)
            val drawingFile = File(drawingPath)
            val drawing = BitmapFactory.decodeFile(drawingFile.absolutePath)
            bitmapMutable.value = drawing
        }
    }

    fun saveDrawing(drawing: ImageBitmap, id: Int, context: Context) {
        //save the image to app's local directory
        //save the path to repository or update the path in the repository
        val fileName = "drawing_${id}.png"
        val file = File(context.filesDir, fileName)
        val bitmap = drawing.asAndroidBitmap()
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        val newDrawing = DrawingEntity(
            drawingPath = file.absolutePath,
            id = id
        )

        hasBeenSavedMutable.value = true

        dao.insertDrawing(newDrawing)
    }

    // Adds a stroke to the list once the user lifts their finger
    fun addStroke(drawingId: Int, newStroke: Stroke) {
        strokesMutable.value += newStroke
        currentStrokeMutable.value = ArrayList<Offset>()
    }

//    fun getStrokes(drawingId: Int): Flow<List<Stroke>> {
//        return dao.getDrawingById(drawingId)
//            .map { drawing -> drawing?.strokes ?: emptyList() }
//    }
    // Adds a point to a list of the current stroke
    fun addPoint(point: Offset) {
        currentStrokeMutable.value = currentStrokeMutable.value + point
    }

    // Changes the color
    fun changeColor(red: Float, green: Float, blue: Float, alpha: Float) {
        currentColorMutable.value = Color(red, green, blue, alpha)
    }

    // Changes the size
    fun changeSize(size: Float) {
        currentSizeMutable.value = size.toInt()
    }

    // Changes the shape
    fun changeShape(type: String) {
        currentShapeMutable.value = type
    }

    // Updates the bitmap when a new line has been created
    fun updateBitmap(thisBitmap: Bitmap)
    {
        bitmapMutable.value = thisBitmap
    }

    fun isNewDrawing(value: Boolean) {
        isNewDrawingMutable.value = value
    }


    // Creates a new Bitmap based on a given Picture
    fun createBitmapFromPicture(picture: Picture): Bitmap {
        val bitmap = createBitmap(picture.width, picture.height)
        val tempCanvas = android.graphics.Canvas(bitmap)
        tempCanvas.drawPicture(picture)
        return bitmap
    }

    fun exportBitmap(drawing: ImageBitmap, id: Int, context: Context) {
        // First save the drawing in the db if the user hasn't already done so
        saveDrawing(drawing, id, context)
        val bytes = ByteArrayOutputStream()
        val bitmap = drawing.asAndroidBitmap()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        val uri = Uri.parse(path.toString())
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    suspend fun deleteDrawing(drawingID: Int) {
        dao.delDrawing(drawingID)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasScreen(navController: NavHostController, drawingId: Int, newDrawing: Boolean, startingImg: String) {
    val myVM: CanvasViewModel = viewModel()

    // observe current stroke, color, size, shape
    val observableCurrentStroke by myVM.currentStrokeReadOnly.collectAsState()
    val observableColor by myVM.currentColorReadOnly.collectAsState()
    val strokeSize by myVM.currentSizeReadOnly.collectAsState()
    val strokeShape by myVM.currentShapeReadOnly.collectAsState()
    val newestBitmap by myVM.bitmapReadOnly.collectAsState()
    val isNewDrawing by myVM.isNewDrawingReadOnly.collectAsState()

    val hasBeenSaved by myVM.hasBeenSavedReadOnly.collectAsState()


    var sizePosition by remember { mutableFloatStateOf(strokeSize.toFloat()) }
    var redPosition by remember { mutableFloatStateOf(strokeSize.toFloat()) }
    var greenPosition by remember { mutableFloatStateOf(strokeSize.toFloat()) }
    var bluePosition by remember { mutableFloatStateOf(strokeSize.toFloat()) }
    var alphaPosition by remember { mutableFloatStateOf(strokeSize.toFloat()) }


    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    val picture = remember { Picture() }
    myVM.isNewDrawing(newDrawing)

    val coroutineScope = rememberCoroutineScope()

    //if it's not a new drawing, update the canvas with the old bitmap from the directory
    if(!newDrawing) {
        LaunchedEffect(drawingId) {
            myVM.updateCanvas(drawingId)
        }
    }

    Column(modifier = Modifier
        .padding(15.dp)
        .fillMaxSize())
    {
        Column (modifier = Modifier
            .padding(top = 20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start){
        Row{
            Button(
                modifier = Modifier.testTag("SaveButton"),
                onClick = { myVM.saveDrawing(newestBitmap.asImageBitmap(), drawingId, navController.context)}) {
                Text("Save")
            }
            Button(
                modifier = Modifier.testTag("ShareButton"),
                onClick = {
                myVM.exportBitmap(newestBitmap.asImageBitmap(), drawingId, navController.context)})
            {
                Text("Share")
            }
            Button(
                modifier = Modifier.testTag("BackButton"),
                onClick = {
                    if (!hasBeenSaved)
                    {
                        coroutineScope.launch {
                            myVM.deleteDrawing(drawingId)
                        }
                    }
                    navController.navigate("main")})
            {
                Text("Back to Main")
            }
        }
            Row {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // This displays the size slider
                    Text(
                        text = "Red: " + redPosition.toInt().toString(), fontSize = 13.sp,
                        modifier = Modifier
                            .padding(end = 20.dp)
                            .testTag("RedTag")
                    )
                    Slider(

                        value = redPosition,
                        onValueChange = {
                            redPosition = it
                            myVM.changeColor(
                                it / 255,
                                observableColor.green,
                                observableColor.blue,
                                observableColor.alpha
                            )
                        },
                        steps = 254,
                        valueRange = 0f..255f,
                        thumb = {
                            SliderDefaults.Thumb(
                                interactionSource = interactionSource,
                                thumbSize = DpSize(20.dp, 20.dp)
                            )
                        }
                    )
                }
            }

            Row {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // This displays the size slider
                    Text(
                        text = "Green: " + greenPosition.toInt().toString(), fontSize = 13.sp,
                        modifier = Modifier
                            .padding(end = 20.dp)
                            .testTag("GreenTag")
                    )
                    Slider(
                        value = greenPosition,
                        onValueChange = {
                            greenPosition = it
                            myVM.changeColor(
                                observableColor.red,
                                it / 255,
                                observableColor.blue,
                                observableColor.alpha
                            )
                        },
                        steps = 254,
                        valueRange = 0f..255f,
                        thumb = {
                            SliderDefaults.Thumb(
                                interactionSource = interactionSource,
                                thumbSize = DpSize(20.dp, 20.dp)
                            )
                        }
                    )
                }
            }

            Row {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // This displays the size slider
                    Text(
                        text = "Blue: " + bluePosition.toInt().toString(), fontSize = 13.sp,
                        modifier = Modifier
                            .padding(end = 20.dp)
                            .testTag("BlueTag")
                    )
                    Slider(
                        value = bluePosition,
                        onValueChange = {
                            bluePosition = it
                            myVM.changeColor(
                                observableColor.red,
                                observableColor.green,
                                it/255,
                                observableColor.alpha
                            )
                        },
                        steps = 254,
                        valueRange = 0f..255f,
                        thumb = {
                            SliderDefaults.Thumb(
                                interactionSource = interactionSource,
                                thumbSize = DpSize(20.dp, 20.dp)
                            )
                        }
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically){
                // This displays the size slider
                Text(text = "Brush size: " + sizePosition.toInt().toString(), fontSize = 13.sp,
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .testTag("BrushTag")
                )
                Slider(
                    value = sizePosition,
                    onValueChange = {sizePosition = it
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
        }
        Column(
            modifier = Modifier
                .drawWithCache {
                    // Example that shows how to redirect rendering to an Android Picture and then
                    // draw the picture into the original destination
                    val width = this.size.width.toInt()
                    val height = this.size.height.toInt()
                    onDrawWithContent {
                        val pictureCanvas =
                            Canvas(
                                picture.beginRecording(
                                    width,
                                    height
                                )
                            )
                        draw(this, this.layoutDirection, pictureCanvas, this.size) {
                            this@onDrawWithContent.drawContent()
                        }
                        picture.endRecording()

                        drawIntoCanvas { canvas -> canvas.nativeCanvas.drawPicture(picture) }
                    }
                }
        ) {
            Canvas(
                modifier = Modifier
                    .testTag("CanvasTag")
                    .border(width = 3.dp, color = Black, shape = CutCornerShape(5.dp))
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
                                myVM.addStroke(drawingId, Stroke(observableCurrentStroke, observableColor, strokeSize, strokeShape))
                                myVM.updateBitmap(myVM.createBitmapFromPicture(picture))
                            }
                        )
                    }

            ) {
                //draw imported image, if it exists
                if(startingImg != "") {
                    val uri = startingImg.toUri()
                    try {
                        val inputStream =
                            uri.let { navController.context.contentResolver.openInputStream(it) }
                        val drawing = BitmapFactory.decodeStream(inputStream)
                        val importedBitmap = drawing.asImageBitmap()
                        inputStream?.close()
                        this.drawImage(importedBitmap, dstSize = IntSize(size.width.toInt(), size.height.toInt()))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // Draws the existing bitmap (the previous strokes)
                this.drawImage(newestBitmap.asImageBitmap())

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

//                if(isNewDrawing) {
//                    myVM.saveDrawing(newestBitmap.asImageBitmap(), drawingId, navController.context)
//                    myVM.isNewDrawing(false)
//                }
            }
        }

    }
}
