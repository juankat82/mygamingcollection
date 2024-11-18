package com.juan.mygamingcollection.ui.navigation

import android.content.Context
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.juan.mygamingcollection.data.firebaseDB.FirebaseDBConnectImpl
import com.juan.mygamingcollection.data.viewmodel.ItemsViewModel
import com.juan.mygamingcollection.data.viewmodel.ScreenViewModel
import com.juan.mygamingcollection.data.viewmodel.UserViewModel
import com.juan.mygamingcollection.ui.screens.AddNewItem
import com.juan.mygamingcollection.ui.screens.Screens
import com.juan.mygamingcollection.ui.screens.UploadImages
import com.juan.mygamingcollection.ui.screens.SeeFullCollection
import com.juan.mygamingcollection.ui.screens.SignInOrRegister

@Composable
fun Navigation(navHostController: NavHostController, context: Context, screenViewModel: ScreenViewModel, userViewModel: UserViewModel, itemsViewModel: ItemsViewModel, firebaseDBConnect: FirebaseDBConnectImpl, drawerState: DrawerState) {
    NavHost(navController = navHostController, startDestination = Screens.BottomScreens.SeeFullCollection.bRoute) {
        composable(route = Screens.BottomScreens.SeeFullCollection.bRoute) {
            SeeFullCollection(userViewModel = userViewModel, context = context, itemsViewModel = itemsViewModel, firebaseDBConnectImpl = firebaseDBConnect, screenViewModel = screenViewModel, drawerState = drawerState)
        }
        composable(route = Screens.BottomScreens.AddNewItem.bRoute) {
            AddNewItem(context, itemsViewModel, firebaseDBConnect, userViewModel, drawerState)
        }
        composable(route = Screens.BottomScreens.UploadImages.bRoute) {
            UploadImages(context, firebaseDBConnect, drawerState)
        }
        composable(route = Screens.OtherScreens.LoginOrRegisterScreen.bRoute) {
            SignInOrRegister(navHostController = navHostController, screenViewModel, userViewModel)
        }
    }
}