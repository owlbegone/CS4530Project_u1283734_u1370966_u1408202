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
import androidx.compose.ui.platform.testTag
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

}

@Composable
fun MainScreen(navController: NavHostController, myVM: MainViewModel = viewModel()) {
    val scope = rememberCoroutineScope()
    val drawingList by myVM.drawingReadOnly.collectAsState(emptyList())

    //val source = ImageDecoder.createSource(ContentResolver.SCHEME_ANDROID_RESOURCE, imageURI)

    Column (modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(40.dp))
        Row{
            Button(
                modifier = Modifier.testTag("NewButton"),
                onClick = {
                    navController.navigate("canvas/-1?newDrawing=true?startingImg=")

                }) {
                Text("New")
            }
            Button(
                modifier = Modifier.testTag("ImportButton"),
                onClick = {
                    navController.navigate("analysis/-1")
                }
            )
            {
                Text("Import")
            }
        }


        Row {
            LazyColumn(
                modifier = Modifier
                    .testTag("SavedColumn"),
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
                                .testTag("SavedImage")
                                .size(300.dp)
                                .clickable { navController.navigate("canvas/${drawingEntity?.id}?newDrawing=false?startingImg=") }
                                .background(Color.Transparent)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(300.dp)
                                .background(Color.Gray)
                                .clickable { navController.navigate("canvas/${drawingEntity?.id}?newDrawing=false?startingImg=") }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

        }
    }}