package com.example.drawingapplication.screens

import android.R.attr.bitmap
import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.drawingapplication.DrawingApp
import com.example.drawingapplication.Model.Stroke
import com.example.drawingapplication.room.DrawingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import kotlin.text.lines


class MainViewModel(application: Application) : AndroidViewModel(application) {
    val dao = (application as DrawingApp).repository
    val drawingReadOnly: Flow<List<DrawingEntity?>> = dao.allDrawings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun getDrawingById(drawingId: Int): ImageBitmap {
        val drawingPath = dao.getDrawingById(drawingId)
        val drawingFile = File(drawingPath)
        val drawing = BitmapFactory.decodeFile(drawingFile.absolutePath)
        return drawing.asImageBitmap()
    }

    suspend fun newDrawing(context: Context): Int {
        val tempDrawing = DrawingEntity(
            drawingPath = "",
        )
        val newId = dao.insertAndReturnId(tempDrawing).toInt()

        val fileName = "drawing_${newId}.png"
        val file = File(context.filesDir, fileName)

        val newDrawing = DrawingEntity(
            drawingPath = file.absolutePath,
            id = newId
        )
        dao.insertDrawing(newDrawing)

        return newDrawing.id
    }


//    suspend fun createNewDrawing(): Long {
//        val newDrawing = DrawingEntity(strokes = ArrayList())
//        return dao.insertDrawing(newDrawing)
//    }

//    fun createNewDrawing(): Int {
//        return dao.addDrawing()
//    }
}

@Composable
fun MainScreen(navController: NavHostController, myVM: MainViewModel = viewModel()) {
    val scope = rememberCoroutineScope()
    val drawingList by myVM.drawingReadOnly.collectAsState(emptyList())
    Column (modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text("this is the main screen")
        Spacer(Modifier.height(20.dp))
        // this should create a new canvas, not navigate to an existing one

//        Button(onClick = {navController.navigate("canvas/${drawing.id}")}) {
        Button(onClick = {
            scope.launch{
                val newDrawing = myVM.newDrawing(navController.context)
                navController.navigate("canvas/${newDrawing}?newDrawing=true")
            }
        }) {
            Text("New Drawing")
        }

        Row {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(drawingList) { drawingEntity ->
                    val drawingFile = File(drawingEntity?.drawingPath)
                    val drawing = BitmapFactory.decodeFile(drawingFile.absolutePath)
                    if (drawing != null) {
                        Image(
                            bitmap = drawing.asImageBitmap(),
                            contentDescription = "Drawing ${drawingEntity?.id}",
                            modifier = Modifier
                                .size(100.dp)
                                .clickable { navController.navigate("canvas/${drawingEntity?.id}?newDrawing=false") }
                                .background(Color.LightGray)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(Color.Gray)
                                .clickable { navController.navigate("canvas/${drawingEntity?.id}") }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

        }
    }}