package com.example.drawingapplication.screens

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.drawingapplication.DrawingApp
import com.example.drawingapplication.room.DrawingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


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
}

@Composable
fun MainScreen(navController: NavHostController, myVM: MainViewModel = viewModel()) {
    val scope = rememberCoroutineScope()
    val drawingList by myVM.drawingReadOnly.collectAsState(emptyList())

    var imageURI by remember { mutableStateOf<Uri?>(null)}
    val mediaPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent())
    {
        uri: Uri? ->
        imageURI = uri
        if (imageURI != null)
        {
            try{
                val inputStream = uri?.let { navController.context.contentResolver.openInputStream(it) }
                val drawing = BitmapFactory.decodeStream(inputStream)
                val importedBitmap = drawing.asImageBitmap()
                inputStream?.close()
                scope.launch {
                    val tempDrawingID = myVM.newDrawing(navController.context)
                    val importedDrawing = myVM.saveDrawing(importedBitmap, tempDrawingID, navController.context)
                    navController.navigate("canvas/${tempDrawingID}?newDrawing=false")
                }
            }
            catch(e:Exception){
                e.printStackTrace()
            }
        }
//        val drawingFile = File(imageURI?.drawingPath)
//        val drawing = BitmapFactory.decodeFile(drawingFile.absolutePath)
    }
    //val source = ImageDecoder.createSource(ContentResolver.SCHEME_ANDROID_RESOURCE, imageURI)

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
        Button(onClick = {
            mediaPicker.launch("image/*")
        })
        {
            Text("Import Photo")
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
                                .size(300.dp)
                                .clickable { navController.navigate("canvas/${drawingEntity?.id}?newDrawing=false") }
                                .background(Color.Transparent)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(300.dp)
                                .background(Color.Gray)
                                .clickable { navController.navigate("canvas/${drawingEntity?.id}") }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

        }
    }}