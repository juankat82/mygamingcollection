package com.juan.mygamingcollection.ui.screens

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.ui.res.stringResource
import com.juan.mygamingcollection.MainActivity
import com.juan.mygamingcollection.R

sealed class Screens(val title: String, val route: String) {

    sealed class BottomScreens(val bTitle: String, val bRoute: String, @DrawableRes val icon: Int) : Screens(bTitle, bRoute) {

        object SeeFullCollection : BottomScreens("See Full Collection", "see_full_collection", R.drawable.visibility_on)

        object AddNewItem : BottomScreens("New Item", "new_item", R.drawable.plus )

        object UploadImages : BottomScreens("Upload Images", "upload", R.drawable.image_upload)
    }

    sealed class OtherScreens(val bTitle: String, val bRoute: String, @DrawableRes val icon: Int) : Screens(bTitle, bRoute) {
        object LoginOrRegisterScreen: OtherScreens("Login/Register", "login_register", R.drawable.login_register)
    }
}

val screensInBottom = listOf(
    Screens.BottomScreens.SeeFullCollection,
    Screens.BottomScreens.AddNewItem,
    Screens.BottomScreens.UploadImages
)

val otherScreens = listOf(
    Screens.OtherScreens.LoginOrRegisterScreen
)