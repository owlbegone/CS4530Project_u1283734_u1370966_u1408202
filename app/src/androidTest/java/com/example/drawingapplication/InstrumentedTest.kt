package com.example.drawingapplication

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.drawingapplication.screens.CanvasScreen
import com.example.drawingapplication.screens.MainScreen

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Before
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class InstrumentedTest {

    private lateinit var navController: TestNavHostController

    @get:Rule
    val composeTestRule = createComposeRule()
    // Then navigate

    @Before
    fun setupNavigator() {

    }


    @Test
    fun testMainScreen() {
        composeTestRule.setContent {
            val testingNavController = rememberNavController()

            MainScreen(testingNavController)
        }

        composeTestRule.onNodeWithText("to canvas").isDisplayed();
    }

    @Test
    fun testColorButtonsRender() {
        composeTestRule.setContent {
            val testingNavController = rememberNavController()

            CanvasScreen(testingNavController)
        }

        composeTestRule.onNodeWithText("Red").isDisplayed();
        composeTestRule.onNodeWithText("Blue").isDisplayed();
        composeTestRule.onNodeWithText("Green").isDisplayed();

    }

    @Test
    fun testBrushSize() {
        composeTestRule.setContent {
            val testingNavController = rememberNavController()

            CanvasScreen(testingNavController)
        }

        composeTestRule.onNodeWithText("Brush Size").isDisplayed();
    }

    @Test
    fun testBrushShape() {
        composeTestRule.setContent {
            val testingNavController = rememberNavController()

            CanvasScreen(testingNavController)
        }

        composeTestRule.onNodeWithText("Brush Shape").isDisplayed();
    }
}