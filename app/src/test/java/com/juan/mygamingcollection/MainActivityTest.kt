package com.juan.mygamingcollection

import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.juan.mygamingcollection.ui.theme.MyGamingCollectionTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainActivityTest {
    @get:Rule
    val composeRule = createComposeRule()

    @get:Rule
    val composeTestRule = createAndroidComposeRule(MainActivity::class.java)
    lateinit var targetContext: Context

    @Before
    fun setUp() {
        targetContext = composeTestRule.activity.context
    }

    @Test
    fun myTest() {
        composeRule.setContent {
            MyGamingCollectionTheme { MainActivity() }
            val loginLabel = composeTestRule.activity.getString(R.string.login)
            composeTestRule.onNodeWithText(loginLabel).performClick()
        }
    }
}