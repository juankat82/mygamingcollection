package com.juan.mygamingcollection.data.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.juan.mygamingcollection.data.authentication.AuthenticationImpl

object UserViewModel : ViewModel() {

    private var _userLiveData = MutableLiveData<FirebaseUser?>()
    val userLiveData: LiveData<FirebaseUser?>
    get() = _userLiveData
    val authentication = AuthenticationImpl
    val auth = Firebase.auth
    val user = Firebase.auth.currentUser

    fun loginUser(context: Context,userName: String, userPassword:String) {
        authentication.attemptToLogin(userName, userPassword, context)
    }

    fun loginUserWithGoogleCredentials(googleIdTokenCredential: GoogleIdTokenCredential, context: Context) {
        authentication.registerUserWithGoogle(googleIdTokenCredential,  this, context)
    }

    fun registerUser(context: Context, userName: String, userPassword: String) {
        authentication.registerNewUser(userName, userPassword, context)
    }

    fun getUser() {
        _userLiveData.value = user
    }
}