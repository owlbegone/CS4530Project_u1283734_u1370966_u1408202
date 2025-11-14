package com.example.drawingapplication.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.drawingapplication.screens.AnalysisScreen
import com.example.drawingapplication.screens.MainScreen
import com.example.drawingapplication.screens.SplashScreen
import com.example.drawingapplication.screens.CanvasScreen

@Composable
fun AppNavHost(navController: NavHostController, startDestination: String="splash")
{
    NavHost(navController=navController, startDestination=startDestination)
    {
        composable("splash") {
           SplashScreen(navController)
        }

        composable("main") {
            MainScreen(navController)
        }



        composable(
            "analysis/{drawingId}",
            arguments = listOf(navArgument("drawingId") { type = NavType.IntType })
            ) {
            backStackEntry ->
            val drawingId = backStackEntry.arguments?.getInt("drawingId") ?: -1
            AnalysisScreen(navController, drawingId)
        }

        composable(
            route = "canvas/{drawingId}?newDrawing={newDrawing}?startingImg={startingImg}",
            arguments = listOf(
                navArgument("drawingId") { type = NavType.IntType },
                navArgument("newDrawing") { type = NavType.BoolType; defaultValue = true },
                navArgument("startingImg") { type = NavType.StringType; defaultValue = ""}
            )
        ) { backStackEntry ->
            val drawingId = backStackEntry.arguments?.getInt("drawingId") ?: -1
            val newDrawing = backStackEntry.arguments?.getBoolean("newDrawing") ?: true
            val startingImg = backStackEntry.arguments?.getString("startingImg") ?: ""

            CanvasScreen(navController, drawingId, newDrawing, startingImg)
        }
    }
}