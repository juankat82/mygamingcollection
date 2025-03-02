package com.juan.mygamingcollection.data.ui.screens

import android.widget.Advanceable
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.IdlingResource
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelectable
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.runComposeUiTest
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource.ResourceCallback
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withSubstring
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.juan.mygamingcollection.MainActivity
import com.juan.mygamingcollection.R
import com.juan.mygamingcollection.data.authentication.AuthenticationImpl
import com.juan.mygamingcollection.data.firebaseDB.FirebaseDBConnectImpl
import com.juan.mygamingcollection.data.preferences.MyPreferences
import com.juan.mygamingcollection.data.roomDB.ItemsRoomDB
import com.juan.mygamingcollection.data.viewmodel.ItemsViewModel
import com.juan.mygamingcollection.data.viewmodel.ScreenViewModel
import com.juan.mygamingcollection.data.viewmodel.UserViewModel
import com.juan.mygamingcollection.ui.navigation.Navigation
import com.juan.mygamingcollection.ui.screens.RegisterScreen
import com.juan.mygamingcollection.ui.screens.Screens
import com.juan.mygamingcollection.ui.screens.SignInOrRegister
import com.juan.mygamingcollection.ui.screens.screensInBottom
import com.juan.mygamingcollection.ui.theme.MyGamingCollectionTheme
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.awaitility.Awaitility
import org.awaitility.Awaitility.await
import org.awaitility.kotlin.untilNotNull
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

@RunWith(AndroidJUnit4::class)
class UITestSignInOrRegister {
    val context = InstrumentationRegistry.getInstrumentation().targetContext

    @get:Rule(order = 0)
    val androidComposeTestRule = createAndroidComposeRule(MainActivity::class.java)

    lateinit var idlingResource: CountingIdlingResource
    var userName: String = ""
    lateinit var navHostController: NavHostController
    lateinit var screenViewModel: ScreenViewModel
    lateinit var userViewModel: UserViewModel
    lateinit var itemsViewModel: ItemsViewModel
    lateinit var firebaseDBConnect: FirebaseDBConnectImpl
    lateinit var drawerState: DrawerState
    lateinit var roomDatabase: ItemsRoomDB
    val testScheduler = TestCoroutineScheduler()
    val testDispatcher = StandardTestDispatcher(testScheduler)
    val testScope = TestScope(testDispatcher)
    val myIdlingResource = CountingIdlingResource("Count")//SimpleIdlingResource("MyIdlingResource")

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
           // standardTestDispatcher = StandardTestDispatcher(TestCoroutineScheduler())
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
        androidComposeTestRule.onNodeWithText(context.getString(R.string.user_email)).performTextInput("iorix98@hotmail.com")
        androidComposeTestRule.onNodeWithText(context.getString(R.string.user_password)).performTextInput("Contrasena_1")
        Thread.sleep(1000)
        androidComposeTestRule.onNodeWithText(context.getString(R.string.login)).performClick()
        Thread.sleep(1000)
    }

    @Test
    fun `test register works`() {
        // Start the app
        androidComposeTestRule.activityRule.scenario.onActivity {

            androidComposeTestRule.activity.setContent {
                MyGamingCollectionTheme {
                    androidComposeTestRule.activity.MainScreen()
                }
            }
        }
        androidComposeTestRule.onNodeWithText(context.getString(R.string.register_a)).assertExists()
        androidComposeTestRule.onNodeWithText(context.getString(R.string.register_b)).assertExists()
        Thread.sleep(1000)
        androidComposeTestRule.onNodeWithText(context.getString(R.string.register_b)).performClick()
        androidComposeTestRule.onNodeWithText(context.getString(R.string.new_user_email)).performTextInput("spanishflytwitter82@gmail.com")
        androidComposeTestRule.onNodeWithText(context.getString(R.string.new_user_password)).performTextInput("Contrasena_12")
        androidComposeTestRule.onNodeWithText(context.getString(R.string.new_user_repeat_password)).performTextInput("Contrasena_12")
        Thread.sleep(1000L)
        androidComposeTestRule.onNodeWithText(context.getString(R.string.new_user_register)).performClick()
        Thread.sleep(1000)
    }


    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalTestApi::class)
    @Test
    fun `test google login works`() {
        //THIS TEST REQUIRES THE APP TO BE LOGGED OUT
        androidComposeTestRule.activity.setContent {
            MyGamingCollectionTheme {
                androidComposeTestRule.activity.MainScreen()
            }
        }

        androidComposeTestRule.onNodeWithTag("login_button").assertExists().performClick()//.printToLog("MYLOG")
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
//    https://medium.com/mesmerhq/use-espressos-idlingresource-for-max-android-test-speed-f2305b28b214

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