package com.example.drawingapplication.screens

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
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
import java.io.FileOutputStream



class AnalysisViewModel(application: Application) : AndroidViewModel(application) {
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

        dao.insertDrawing(newDrawing)
    }
//
}

@Composable
fun AnalysisScreen(navController: NavHostController, drawingId: Int, newDrawing: Boolean) {
    val myVM: CanvasViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val newestBitmap by myVM.bitmapReadOnly.collectAsState()




//        val drawingFile = File(imageURI?.drawingPath)
//        val drawing = BitmapFactory.decodeFile(drawingFile.absolutePath)

    Column(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxSize()
    )
    {
        Column(
            modifier = Modifier
                .padding(top = 20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Row {
//            Text(text = BuildConfig.API_KEY, color = Color.Red)

                Button(
                    modifier = Modifier.testTag("BackButton"),
                    onClick = { navController.navigate("main") })
                {
                    Text("Back to Main")
                }


            }

            Canvas(
                modifier = Modifier
                    .testTag("CanvasTag")
                    .border(width = 3.dp, color = Black, shape = CutCornerShape(5.dp))
                    .fillMaxSize()
                    .background(Color.White)


            ) {
                // Draws the existing bitmap (the previous strokes)
                this.drawImage(newestBitmap.asImageBitmap())
            }

        }
    }
}
