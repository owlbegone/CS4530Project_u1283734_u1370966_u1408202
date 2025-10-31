package com.example.drawingapplication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
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
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    }


    @Test
    fun testMainScreenNewDrawingButton() {
        composeTestRule.setContent {
            val testingNavController = rememberNavController()

            MainScreen(testingNavController)
        }
        val button = composeTestRule.onNode(hasTestTag("NewButton"), useUnmergedTree = true)
        button.assertIsDisplayed()
    }

    @Test
    fun testMainScreenImportPhotoButton() {
        composeTestRule.setContent {
            val testingNavController = rememberNavController()

            MainScreen(testingNavController)
        }

        val button = composeTestRule.onNode(hasTestTag("ImportButton"), useUnmergedTree = true)
        button.assertIsDisplayed()
    }

    @Test
    fun testMainScreenSavedColumn() {
        composeTestRule.setContent {
            val testingNavController = rememberNavController()

            MainScreen(testingNavController)
        }

        val lazyColumn = composeTestRule.onNode(hasTestTag("SavedColumn"), useUnmergedTree = true)
        lazyColumn.assertExists()
    }

    @Test
    fun testColorButtonsRender() {
        composeTestRule.setContent {
            val testingNavController = rememberNavController()

            CanvasScreen(testingNavController, 1, true)
        }

        composeTestRule.onNodeWithText("Red: 4").assertIsDisplayed()
        composeTestRule.onNodeWithText("Blue: 4").assertIsDisplayed()
        composeTestRule.onNodeWithText("Green: 4").assertIsDisplayed()

    }

    @Test
    fun testBrushSize() {
        composeTestRule.setContent {
            val testingNavController = rememberNavController()

            CanvasScreen(testingNavController, 1, true)
        }

        composeTestRule.onNodeWithText("Brush size: 4").assertIsDisplayed();
    }

    @Test
    fun testBrushShape() {
        composeTestRule.setContent {
            val testingNavController = rememberNavController()

            CanvasScreen(testingNavController, 1, true)
        }

        composeTestRule.onNodeWithText("Brush shape: Square").assertIsDisplayed();
    }
}