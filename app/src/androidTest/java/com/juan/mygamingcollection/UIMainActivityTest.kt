package com.juan.mygamingcollection

import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnitRunner
import com.juan.mygamingcollection.ui.theme.MyGamingCollectionTheme
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.internal.builders.JUnit4Builder
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UIMainActivityTest {

    val context = InstrumentationRegistry.getInstrumentation().targetContext

//    @get:Rule(order = 0)
//    val composeTestRule = createComposeRule()

    @get:Rule(order = 0)
    val androidComposeTestRule = createAndroidComposeRule(MainActivity::class.java)

    @Test
    fun myTest() {
        // Start the app

        androidComposeTestRule.activity.setContent {

            MyGamingCollectionTheme {
                androidComposeTestRule.activity.MainScreen()

            }
        }

        val loginLabel = androidComposeTestRule.activity.getString(R.string.login)
        androidComposeTestRule.onNodeWithText(loginLabel).performClick()
    }


//
//    @After
//    fun tearDown() {
//    }
//
//    @Test
//    fun getRoomDatabase() {
//    }
//
//    @Test
//    fun setRoomDatabase() {
//    }
//
//    @Test
//    fun getScreenViewModel() {
//    }
//
//    @Test
//    fun setScreenViewModel() {
//    }
//
//    @Test
//    fun getItemsViewModel() {
//    }
//
//    @Test
//    fun setItemsViewModel() {
//    }
//
//    @Test
//    fun getFirebaseUserController() {
//    }
//
//    @Test
//    fun setFirebaseUserController() {
//    }
//
//    @Test
//    fun getFirebaseDBConnect() {
//    }
//
//    @Test
//    fun setFirebaseDBConnect() {
//    }
//
//    @Test
//    fun mainScreen() {
//    }
//
//    @Test
//    fun showExitDialog() {
//    }
//
//    @Test
//    fun createDrawer() {
//    }
}