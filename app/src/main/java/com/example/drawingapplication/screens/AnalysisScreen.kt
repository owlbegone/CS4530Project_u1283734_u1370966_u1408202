package com.example.drawingapplication.screens

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.drawingapplication.DrawingApp
import com.example.drawingapplication.Model.BoundingPoly
import com.example.drawingapplication.Model.ImageStats
import com.example.drawingapplication.Model.NormalizedVertex
import com.example.drawingapplication.Model.Stroke
import com.example.drawingapplication.room.DrawingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.collections.iterator


class AnalysisViewModel(application: Application) : AndroidViewModel(application) {
    val dao = (application as DrawingApp).repository

    fun analyzeDrawing(bitmap: Bitmap): LiveData<ImageStats> {
        val result = MutableLiveData<ImageStats>()
        viewModelScope.launch {
            val imageStats = dao.analyzeDrawing(bitmap)
            Log.e("In Analysis", imageStats.toString())
//            for (item in imageStats.labels) {
//                Log.e("Name Check", item.key)
//                Log.e("Score Check", item.value.first.toString())
//                Log.e("Location Check", item.value.second.toString())
//            }
            result.postValue(imageStats)
        }
        return result
    }
}

@Composable
fun AnalysisScreen(navController: NavHostController, drawingId: Int, myVM: AnalysisViewModel = viewModel()) {
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var imageURI by remember { mutableStateOf<Uri?>(null)}

//    lateinit var drawing : Bitmap
    val lifecycleOwner = LocalLifecycleOwner.current

//    var imageStats by remember{ mutableStateOf<ImageStats> (null)}
    var name by remember { mutableStateOf<String?>(null) }
    var score by remember { mutableStateOf<String?>(null) }
    var location by remember { mutableStateOf<List<NormalizedVertex>>(emptyList()) }

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

                myVM.analyzeDrawing(drawing).observe(lifecycleOwner, Observer { returnedrepo ->
                    for (item in returnedrepo.labels) {
                        name = item.key
                        score = item.value.first.toString()
                        location = item.value.second.normalizedVertices

//                Log.e("Name Check", name.toString())
//                Log.e("Score Check", score.toString())
//                Log.e("Location Check", location.toString())
//
                    }
                })

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
            if (imageURI != null) {

                Canvas(
                    modifier = Modifier.fillMaxWidth()
                )
                {
                    drawLine(
                        start = Offset(
                            (location.first().x?.toFloat() ?: Float) as Float,
                            (location.first().y?.toFloat() ?: Float) as Float,
                        ),
                        end = Offset(
                            (location[2].x?.toFloat() ?: Float) as Float,
                            (location[2].y?.toFloat() ?: Float) as Float
                    ),
                        color = Color.Blue)

                }
            }
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
            Text("Image Statistics: ")
                Text("Object in Image: " + name.toString())
                Text("Confidence: " + score.toString())



            Button(
                modifier =  Modifier.testTag("pickPhoto") .padding(top = 40.dp),
                onClick = {

                    navController.navigate("canvas/${drawingId}?newDrawing=true?startingImg=${imageURI.toString()}")
                }) {
                Text("Start Drawing!")
            }


        }

    }

}
