package com.juan.mygamingcollection

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.juan.mygamingcollection.ui.theme.MyGamingCollectionTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
@HiltAndroidTest
class MainActivityTest {



    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeRule = createComposeRule()// createAndroidComposeRule(MainActivity::class.java)

    @get:Rule
    val composeTestRule = createAndroidComposeRule(MainActivity::class.java)

    lateinit var targetContext: Context

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun myTest() {
        // Start the app
       // val scenario = launchcomposeTestRule.activityRule.scenario.launchActivity<MainActivity>()
        composeRule.setContent {
           // MyGamingCollectionTheme {
                //targetContext = composeTestRule.activity.context
                MainScreen()

//            }
        }
        val loginLabel = composeTestRule.activity.getString(R.string.login)
        composeTestRule.onNodeWithText(loginLabel).performClick()
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