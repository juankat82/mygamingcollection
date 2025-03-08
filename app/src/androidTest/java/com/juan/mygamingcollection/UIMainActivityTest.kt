package com.juan.mygamingcollection

import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.juan.mygamingcollection.ui.theme.MyGamingCollectionTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UIMainActivityTest {
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
}