package com.example.drawingapplication.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
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
            route = "canvas/{drawingId}",
            arguments = listOf(navArgument("drawingId") { type = NavType.IntType })
        ) { backStackEntry ->
            val drawingId = backStackEntry.arguments?.getInt("drawingId") ?: -1
            CanvasScreen(navController, drawingId)
        }
    }
}