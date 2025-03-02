package com.juan.mygamingcollection.data.authentication

import android.content.Context
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.juan.mygamingcollection.data.viewmodel.ScreenViewModel
import com.juan.mygamingcollection.data.viewmodel.UserViewModel

interface IAuthentication {

    fun checkUserIsLoggedIn() : Boolean

    fun attemptToLogin(userViewModel: UserViewModel, userName:String, userPassword:String, context: Context, screenViewModel: ScreenViewModel) :Int

    fun registerNewUser(userName:String, userPassword:String, context: Context, userViewModel: UserViewModel)

    fun logout()

    fun registerUserWithGoogle(googleIdTokenCredential: GoogleIdTokenCredential, userViewModel: UserViewModel, context: Context, userPassword: String)

    fun resetPassword(email: String, context: Context)
}