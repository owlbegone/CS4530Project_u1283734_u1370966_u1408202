package com.example.drawingapplication.screens

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.drawingapplication.DrawingApp
import com.example.drawingapplication.Model.ImageStats
import kotlinx.coroutines.launch


class AnalysisViewModel(application: Application) : AndroidViewModel(application) {
    val dao = (application as DrawingApp).repository

    suspend fun analyzeDrawing(bitmap: Bitmap): ImageStats {
        val imageStats = dao.analyzeDrawing(bitmap)
        Log.e("In Analysis", imageStats.toString())
//        for (item in imageStats.labels) {
//            Log.e("Name Check", item.key)
//            Log.e("Score Check", item.value.first.toString())
//            Log.e("Location Check", item.value.second.toString())
//        }
        return imageStats
    }

    suspend fun deleteDrawing(drawingID: Int) {
        dao.delDrawing(drawingID)
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AnalysisScreen(navController: NavHostController, drawingId: Int, myVM: AnalysisViewModel = viewModel()) {
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var imageURI by remember { mutableStateOf<Uri?>(null)}

    val coroutineScope = rememberCoroutineScope()

    var imageStats by remember{ mutableStateOf<ImageStats>(ImageStats())}

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
                inputStream?.close()

                coroutineScope.launch{
                    val stats = myVM.analyzeDrawing(drawing)
                    imageStats = stats
                    Log.e("AFTER ANALYSIS", "IMAGE STATS SET")
                }
            }
            catch(e:Exception){
                e.printStackTrace()
            }
        }
    }



    Column(modifier = Modifier.fillMaxSize().padding( 20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){

        BoxWithConstraints(contentAlignment = Alignment.Center)
        {
            bitmap?.let {
                img ->
                val width = img.width
                val height = img.height

                val widthToHeightScale = width.toFloat()/height.toFloat()

                val thisHeight = maxWidth.value / widthToHeightScale

                var imageModifier = Modifier.fillMaxWidth().height(thisHeight.dp)

                if (width < height)
                {
                    imageModifier = Modifier.height(400.dp).width((400 * widthToHeightScale).dp)
                }
                Image(
                    bitmap = img,
                    contentDescription = null,
                    modifier = imageModifier
                )
                Canvas(
                    modifier = imageModifier
                )
                {
                    //drawImage()
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    Log.e("Canvas Size", "$canvasWidth and $canvasHeight")
                    for (label in imageStats.labels)
                    {
                        if (label.name != "None")
                        {
                            val vertexOne = label.boundingPoly.normalizedVertices[0]
                            val vertexTwo = label.boundingPoly.normalizedVertices[1]
                            val vertexThree = label.boundingPoly.normalizedVertices[2]
                            val vertexFour = label.boundingPoly.normalizedVertices[3]

                            Log.e("Vertices", "$vertexOne $vertexTwo $vertexThree $vertexFour")

                            // draws one line between vertices one and two
                            drawLine(
                                start = Offset(x = vertexOne.x * canvasWidth, y = vertexOne.y * canvasHeight),
                                end = Offset(x = vertexTwo.x * canvasWidth, y = vertexTwo.y * canvasHeight),
                                color = Color.Red,
                                strokeWidth = 5f
                            )
                            // draws one line between vertices two and three
                            drawLine(
                                start = Offset(x = vertexTwo.x * canvasWidth, y = vertexTwo.y * canvasHeight),
                                end = Offset(x = vertexThree.x * canvasWidth, y = vertexThree.y * canvasHeight),
                                color = Color.Red,
                                strokeWidth = 5f
                            )
                            // draws one line between vertices three and four
                            drawLine(
                                start = Offset(x = vertexThree.x * canvasWidth, y = vertexThree.y * canvasHeight),
                                end = Offset(x = vertexFour.x * canvasWidth, y = vertexFour.y * canvasHeight),
                                color = Color.Red,
                                strokeWidth = 5f
                            )
                            // draws one line between vertices two and three
                            drawLine(
                                start = Offset(x = vertexOne.x * canvasWidth, y = vertexOne.y * canvasHeight),
                                end = Offset(x = vertexFour.x * canvasWidth, y = vertexFour.y * canvasHeight),
                                color = Color.Red,
                                strokeWidth = 5f
                            )

                        }
                    }
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
            Text(color = Color.Red, text = "Image Statistics: ")
            Column{
                var labelsUsed = mutableListOf<String>()
                Log.e("Current ImageStats", imageStats?.labels.toString())
                for (label in imageStats.labels) {
                    Row {
                        Text(color = Color.Red, text = label.name + ": ")
                        Text(color = Color.Red, text = label.score.toString())
                        labelsUsed.add(label.name)
                    }
                }
                for (label in imageStats.labelsNoLoc)
                {
                    Row {
                        if (!labelsUsed.contains(label.description))
                        {
                            Text(color = Color.Magenta, text = label.description + ": ")
                            Text(color = Color.Magenta, text = label.score.toString())
                        }
                    }
                }
            }



            Button(
                modifier =  Modifier.testTag("pickPhoto") .padding(top = 40.dp),
                onClick = {

                    navController.navigate("canvas/${drawingId}?newDrawing=true?startingImg=${imageURI.toString()}")
                }) {
                Text("Start Drawing!")
            }
        }
        Button(
            modifier = Modifier.testTag("BackToMain") .padding(10.dp),
            onClick = {
                coroutineScope.launch {
                    myVM.deleteDrawing(drawingId)
                }
                navController.navigate("main")
            }
        )
        {Text("Back")}

    }

}
