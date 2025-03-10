/*
 *  Resources at:  https://medium.com/mesmerhq/use-espressos-idlingresource-for-max-android-test-speed-f2305b28b214
*/
package com.juan.mygamingcollection.data.ui.screens

import androidx.activity.compose.setContent
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.juan.mygamingcollection.MainActivity
import com.juan.mygamingcollection.R
import com.juan.mygamingcollection.data.firebaseDB.FirebaseDBConnectImpl
import com.juan.mygamingcollection.data.roomDB.ItemsRoomDB
import com.juan.mygamingcollection.data.viewmodel.ItemsViewModel
import com.juan.mygamingcollection.data.viewmodel.ScreenViewModel
import com.juan.mygamingcollection.data.viewmodel.UserViewModel
import com.juan.mygamingcollection.ui.theme.MyGamingCollectionTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.awaitility.Awaitility.await
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class UITestSignInOrRegister {

    @get:Rule(order = 0)
    val androidComposeTestRule = createAndroidComposeRule(MainActivity::class.java)

    val context = InstrumentationRegistry.getInstrumentation().targetContext
    lateinit var idlingResource: CountingIdlingResource
    lateinit var navHostController: NavHostController
    lateinit var screenViewModel: ScreenViewModel
    lateinit var userViewModel: UserViewModel
    lateinit var itemsViewModel: ItemsViewModel
    lateinit var firebaseDBConnect: FirebaseDBConnectImpl
    lateinit var drawerState: DrawerState
    lateinit var roomDatabase: ItemsRoomDB
    val testScheduler = TestCoroutineScheduler()
    val myIdlingResource = CountingIdlingResource("Count")
    @Before
    fun initialize() {
        idlingResource = CountingIdlingResource("CountingIdling",false)
        IdlingPolicies.setMasterPolicyTimeout(10, TimeUnit.SECONDS)
        IdlingPolicies.setIdlingResourceTimeout(10, TimeUnit.SECONDS)
        IdlingRegistry.getInstance().register(myIdlingResource)

        androidComposeTestRule.activity.setContent {
            roomDatabase = Room.databaseBuilder(context,ItemsRoomDB::class.java, "items").fallbackToDestructiveMigration().build()
            navHostController = rememberNavController()
            androidComposeTestRule.activity.navHostController = navHostController
            screenViewModel = viewModel()
            userViewModel = viewModel()
            itemsViewModel = viewModel()
            firebaseDBConnect= FirebaseDBConnectImpl(roomDatabase, itemsViewModel)
            firebaseDBConnect.db
            screenViewModel.setScreen(0)
            drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        }
    }

    @After
    fun finish() {
        IdlingRegistry.getInstance().unregister(myIdlingResource)
    }

    @Test
    fun `test Login works`() {
        // Start the app
        androidComposeTestRule.activity.setContent {
            MyGamingCollectionTheme {
                androidComposeTestRule.activity.MainScreen()
            }
        }
        androidComposeTestRule.onNodeWithText(context.getString(R.string.login_signin_title)).assertTextEquals("Login/Sign-In")
        androidComposeTestRule.onNodeWithText(context.getString(R.string.user_email)).assertExists()
        androidComposeTestRule.onNodeWithText(context.getString(R.string.user_password)).assertExists()
        androidComposeTestRule.onNodeWithText(context.getString(R.string.login)).assertExists()
        androidComposeTestRule.onNodeWithText(context.getString(R.string.sign_in)).assertTextEquals("Sign In")
        androidComposeTestRule.onNodeWithText(context.getString(R.string.register_a)).assertExists()
        androidComposeTestRule.onNodeWithText(context.getString(R.string.register_b)).assertExists()
        androidComposeTestRule.onNodeWithText(context.getString(R.string.user_email)).performTextInput("myemailaddress@emailserver.xxx")
        androidComposeTestRule.onNodeWithText(context.getString(R.string.user_password)).performTextInput("MyPassword_007")
        Thread.sleep(1000)
        androidComposeTestRule.onNodeWithText(context.getString(R.string.login)).performClick()
        Thread.sleep(1000)
    }

    @Test
    fun `test register works`() {
        // Start the app
        androidComposeTestRule.activityRule.scenario.onActivity {
            androidComposeTestRule.activity.setContent {
                MyGamingCollectionTheme { androidComposeTestRule.activity.MainScreen() }
            }
        }
        androidComposeTestRule.onNodeWithText(context.getString(R.string.register_a)).assertExists()
        androidComposeTestRule.onNodeWithText(context.getString(R.string.register_b)).assertExists()
        Thread.sleep(1000)
        androidComposeTestRule.onNodeWithText(context.getString(R.string.register_b)).performClick()
        androidComposeTestRule.onNodeWithText(context.getString(R.string.new_user_email)).performTextInput("myemailaddress@emailserver.xxx")
        androidComposeTestRule.onNodeWithText(context.getString(R.string.new_user_password)).performTextInput("MyPassword_007")
        androidComposeTestRule.onNodeWithText(context.getString(R.string.new_user_repeat_password)).performTextInput("MyPassword_007")
        Thread.sleep(1000L)
        androidComposeTestRule.onNodeWithText(context.getString(R.string.new_user_register)).performClick()
        Thread.sleep(1000)
    }

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalTestApi::class)
    @Test
    fun `test google login works`() {
        //THIS TEST REQUIRES THE APP TO BE LOGGED OUT
        androidComposeTestRule.activity.setContent {
            MyGamingCollectionTheme { androidComposeTestRule.activity.MainScreen() }
        }

        androidComposeTestRule.onNodeWithTag("login_button").assertExists().performClick()
        val IODispatcher = UnconfinedTestDispatcher()
        runTest(IODispatcher) {
            IODispatcher.scheduler.advanceUntilIdle()
        }
        //AT THIS POINT WE WILL HAVE TO PRESS THE USER WE WANT TO LOGIN
        androidComposeTestRule.waitForIdle()
        Thread.sleep(3000)
        await().during(5, TimeUnit.SECONDS)

       androidComposeTestRule.onNodeWithText(context.getString(R.string.item_name)).assertExists()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test screens exists`(){
        androidComposeTestRule.activity.setContent {
            MyGamingCollectionTheme {
                androidComposeTestRule.activity.MainScreen()
            }
        }
        androidComposeTestRule.onNodeWithTag("login_button").assertExists().performClick()
        val IODispatcher = UnconfinedTestDispatcher()
        runTest(IODispatcher) {
            IODispatcher.scheduler.advanceUntilIdle()
        }
        androidComposeTestRule.waitForIdle()
        Thread.sleep(3000)
        await().during(10, TimeUnit.SECONDS)
        androidComposeTestRule.onNodeWithText(context.getString(R.string.see_full_collection)).assertIsDisplayed()
        Thread.sleep(1000)
        androidComposeTestRule.onNodeWithText(context.getString(R.string.add_new_item)).assertIsDisplayed()
        Thread.sleep(1000)
        androidComposeTestRule.onNodeWithText(context.getString(R.string.upload_new_pictures)).assertIsDisplayed()
        Thread.sleep(1000)
    }

    @Test
    fun `test screens are selectable`() {
        androidComposeTestRule.activity.setContent {
            MyGamingCollectionTheme {
                androidComposeTestRule.activity.MainScreen()
            }
        }
        androidComposeTestRule.onNodeWithTag("login_button").assertExists().performClick()

        await().pollDelay(3, TimeUnit.SECONDS).until {
            androidComposeTestRule.onNodeWithText("New Item").assertExists().performClick()

            androidComposeTestRule.onNodeWithText("Upload pictures").assertExists().performClick()

            androidComposeTestRule.onNodeWithText("See collection").assertExists().performClick()
            true
        }
        await().pollDelay(3, TimeUnit.SECONDS)
    }

    @Test
    fun `check drawer works`() {
        androidComposeTestRule.activity.setContent {
            MyGamingCollectionTheme {
                androidComposeTestRule.activity.MainScreen()
            }
        }
        androidComposeTestRule.onNodeWithTag("login_button").assertExists().performClick()

        await().pollDelay(5, TimeUnit.SECONDS).until {
            androidComposeTestRule.onNodeWithContentDescription("open_close_drawer").assertIsDisplayed().performClick()
            System.out.println("IT-WORKS")
            true
        }
        await().pollDelay(3, TimeUnit.SECONDS).until { true }
        Thread.sleep(3000)
    }
}