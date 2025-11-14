package com.example.drawingapplication.screens

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
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

    fun analyzeDrawing(bitmap: Bitmap) {
        viewModelScope.launch {
            val imageStats = dao.analyzeDrawing(bitmap)
            Log.e("In Analysis", imageStats.toString())
            for (item in imageStats.labels) {
                Log.e("Name Check", item.key)
                Log.e("Score Check", item.value.first.toString())
                Log.e("Location Check", item.value.second.toString())
            }
        }
    }
}

@Composable
fun AnalysisScreen(navController: NavHostController, drawingId: Int, myVM: AnalysisViewModel = viewModel()) {
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
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
                bitmap = drawing.asImageBitmap()
                myVM.analyzeDrawing(drawing)
                inputStream?.close()
            }
            catch(e:Exception){
                e.printStackTrace()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
        bitmap?.let {
            Image(
                bitmap = it,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Button(
            modifier = Modifier.testTag("ImportButton"),
            onClick = {
                mediaPicker.launch("image/*")
            })
        {
            Text("Import Photo")
        }
        if (imageURI != null) {
            Button(
                modifier = Modifier.testTag("pickPhoto"),
                onClick = {

                    navController.navigate("canvas/${drawingId}?newDrawing=true?startingImg=${imageURI.toString()}")
                }) {
                Text("Start Drawing!")
            }
        }

    }

}
