package com.juan.mygamingcollection

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.juan.mygamingcollection.data.authentication.AuthenticationImpl
import com.juan.mygamingcollection.data.firebaseDB.FirebaseDBConnectImpl
import com.juan.mygamingcollection.data.preferences.MyPreferences
import com.juan.mygamingcollection.data.roomDB.ItemsRoomDB
import com.juan.mygamingcollection.data.viewmodel.ItemsViewModel
import com.juan.mygamingcollection.data.viewmodel.ScreenViewModel
import com.juan.mygamingcollection.data.viewmodel.UserViewModel
import com.juan.mygamingcollection.ui.composables.BottomBar
import com.juan.mygamingcollection.ui.composables.DrawerBar
import com.juan.mygamingcollection.ui.composables.TopBar
import com.juan.mygamingcollection.ui.navigation.Navigation
import com.juan.mygamingcollection.ui.screens.Screens
import com.juan.mygamingcollection.ui.screens.backgroundColor
import com.juan.mygamingcollection.ui.screens.otherScreens
import com.juan.mygamingcollection.ui.screens.screensInBottom
import com.juan.mygamingcollection.ui.theme.MyGamingCollectionTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    lateinit var roomDatabase: ItemsRoomDB
    lateinit var screenViewModel: ScreenViewModel
    lateinit var itemsViewModel: ItemsViewModel
    lateinit var navHostController: NavHostController
    val context: Context = this
    val authentication = AuthenticationImpl()
    lateinit var firebaseUserController: AuthenticationImpl
    lateinit var firebaseDBConnect: FirebaseDBConnectImpl
    lateinit var drawerState: DrawerState
    lateinit var currentUser: MutableState<FirebaseUser?>
    lateinit var userViewModel: UserViewModel
    lateinit var drawerEnabled: MutableState<Boolean>
    lateinit var showExitDialog: MutableState<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        roomDatabase = Room.databaseBuilder(applicationContext,ItemsRoomDB::class.java, "items").fallbackToDestructiveMigration().build()
        setContent {
            MyGamingCollectionTheme{
                MainScreen()
            }
        }
    }
    @Composable
    fun MainScreen() {
        screenViewModel = viewModel()
        drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        screenViewModel.setDrawerOpen(drawerState)
        itemsViewModel = viewModel()
        navHostController = rememberNavController()
        firebaseUserController = AuthenticationImpl()
        val scope = rememberCoroutineScope()
        currentUser = remember { mutableStateOf(firebaseUserController.user) }
        firebaseDBConnect = FirebaseDBConnectImpl(roomDatabase, itemsViewModel)
        firebaseDBConnect.db
        screenViewModel.setScreen(0)

        drawerEnabled = remember { mutableStateOf(false) }
        userViewModel = UserViewModel(authentication)
        var isGoogleTokenLogin by rememberSaveable { mutableStateOf(false) }
        showExitDialog = rememberSaveable { mutableStateOf(false) }

        BackHandler {
            if (drawerState.isOpen)
                scope.launch { drawerState.close() }
            if (drawerState.isClosed)
                showExitDialog.value = true
        }

        if (showExitDialog.value)
            ShowExitDialog(context = context)

        if (firebaseUserController.getCurrentConnectivityState(navHostController.context)) {
            Firebase.auth.addAuthStateListener(object : FirebaseAuth.AuthStateListener {
                override fun onAuthStateChanged(auth: FirebaseAuth) {
                    if (!auth.currentUser?.email.isNullOrBlank()) {
                        currentUser.value = auth.currentUser
                        drawerEnabled.value = true
                    }
                }
            })
            userViewModel.userLiveData.observe(context as MainActivity, object : Observer<FirebaseUser?> {
                    override fun onChanged(value: FirebaseUser?) {
                        if (value != null) {
                            if (value.email!!.isNotEmpty()) {
                                drawerEnabled.value = true
                            }
                        }
                        if (value?.email.isNullOrEmpty()) {
                            drawerEnabled.value = false
                            screenViewModel.setScreen(3)
                            navHostController.navigate(Screens.OtherScreens.LoginOrRegisterScreen.bRoute)
                        }
                    }
                })
            LaunchedEffect(Unit) {
                GlobalScope.launch {
                    isGoogleTokenLogin = MyPreferences.shared.getIsGoogleLogin()
                }
            }
            if (drawerEnabled.value) {
                CreateDrawer(
                    context = context,
                    userViewModel = userViewModel,
                    itemsViewModel = itemsViewModel,
                    firebaseDBConnect = firebaseDBConnect,
                    screenViewModel = screenViewModel,
                    drawerEnabled = drawerEnabled.value,
                    drawerState = drawerState,
                    currentUser = currentUser,
                    navHostController = navHostController,
                    scope = scope
                )
            } else {
                Navigation(
                    navHostController,
                    context,
                    screenViewModel,
                    userViewModel,
                    itemsViewModel,
                    firebaseDBConnect,
                    drawerState
                )
                if (currentUser.value == null) {
                    screenViewModel.setScreen(3)
                    navHostController.navigate(otherScreens.get(0).bRoute)
                } else {
                    navHostController.navigate(screensInBottom.get(0).bRoute)
                }
            }

            itemsViewModel.setAuth(firebaseUserController)
            MyPreferences.shared.initialize(LocalContext.current)
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .padding(top = 50.dp, bottom = 15.dp),
                    text = stringResource(id = R.string.you_arent_conected_to_net),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
                Button(modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        (context as MainActivity).finish()
                        val intent = Intent(context, MainActivity::class.java)
                        ContextCompat.startActivity(context, intent, null)
                    }) { Text(text = stringResource(id = R.string.restart_app)) }
                }
            }
        }

    @Composable
    fun ShowExitDialog(context: Context) {
        var isOpenExitDialog by rememberSaveable { mutableStateOf(true) }
        if (isOpenExitDialog) {
            AlertDialog(
                modifier = Modifier,
                title = { Text(text = stringResource(id = R.string.leave_title)) },
                text = { Text(text = stringResource(id = R.string.would_you_leave)) },
                shape = RectangleShape,
                containerColor = backgroundColor,
                onDismissRequest = { isOpenExitDialog = false },
                confirmButton = {
                    Button(onClick = {
                        isOpenExitDialog = false
                        (context as MainActivity).finish()
                    }) { Text(stringResource(android.R.string.ok)) }
                },
                dismissButton = {
                    Button(onClick = { isOpenExitDialog = false }) {
                        Text(stringResource(android.R.string.cancel))
                    }
                },
                icon = { Icon(painterResource(id = R.drawable.logout_icon), "exit_icon") }
            )
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "RestrictedApi")
    @Composable
    fun CreateDrawer(
        context: Context,
        userViewModel: UserViewModel,
        itemsViewModel: ItemsViewModel,
        firebaseDBConnect: FirebaseDBConnectImpl,
        screenViewModel: ScreenViewModel,
        drawerEnabled: Boolean,
        drawerState: DrawerState,
        currentUser: MutableState<FirebaseUser?>,
        navHostController: NavHostController,
        scope: CoroutineScope
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                if (drawerEnabled) {
                    ModalDrawerSheet(modifier = Modifier.height(IntrinsicSize.Min)) {
                        DrawerBar(
                            drawerState,
                            currentUser,
                            context,
                            userViewModel,
                            itemsViewModel,
                            firebaseDBConnect
                        )
                    }
                }
            }) {
            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentUser.value != null)
                            BottomBar(navHostController, screenViewModel, currentUser)
                    },
                    topBar = {
                        if (drawerEnabled) {
                            if (screenViewModel.getScreens() != 3) {
                                TopBar(
                                    onNavigationIconClick = {
                                        drawerState.apply {
                                            scope.launch {
                                                if (isOpen)
                                                    close()
                                                else
                                                    open()
                                                screenViewModel.setDrawerOpen(drawerState)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    },
                    content = { innerPadding ->
                        Navigation(
                            navHostController,
                            context,
                            screenViewModel,
                            userViewModel,
                            itemsViewModel,
                            firebaseDBConnect,
                            drawerState
                        )
                        if (currentUser.value == null) {
                            screenViewModel.setScreen(3)
                            navHostController.navigate(otherScreens.get(0).bRoute) { popUpTo(0) }
                        } else {
                            navHostController.navigate(screensInBottom.get(0).bRoute) { popUpTo(0) }
                        }
                    }
                )
            }
        }
    }
}