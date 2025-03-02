package com.juan.mygamingcollection.data.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.juan.mygamingcollection.data.authentication.AuthenticationImpl
import com.juan.mygamingcollection.data.preferences.MyPreferences
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserViewModel(authenticationImpl: AuthenticationImpl) : ViewModel() {

    val authentication = authenticationImpl
    val auth = Firebase.auth
    private var _user = MutableLiveData<String>()
    private var _userLiveData = MutableLiveData<FirebaseUser?>()
    val userLiveData: LiveData<FirebaseUser?>
    get() = _userLiveData

    private var _syncTimesLiveData = MutableLiveData<Int>()
    val syncTimesLiveData: LiveData<Int>
        get() = _syncTimesLiveData

    fun setSyncTimes(times: Int) {
        _syncTimesLiveData.value = times
    }
    fun setUser(userName: String) : String{
        _user.value = userName.split("@").get(0)
        return userName.split("@").get(0)
    }

    fun loginUser(context: Context,userName: String, userPassword:String, screenViewModel: ScreenViewModel) {
        _user.value = userName.split("@").get(0)
        authentication.attemptToLogin(this, userName, userPassword, context, screenViewModel)
        GlobalScope.launch {
            MyPreferences.shared.setCurrentUserEmail(userName.split("@").get(0))
        }
    }

    fun logoutUser() {
        authentication.logout()
    }

    fun loginUserWithGoogleCredentials(googleIdTokenCredential: GoogleIdTokenCredential, context: Context, userName: String) {
        googleIdTokenCredential.id
        authentication.registerUserWithGoogle(googleIdTokenCredential,  this, context, userName)
        _user.value =  userName.split("@").get(0)
        GlobalScope.launch {
            MyPreferences.shared.setCurrentUserEmail(userName.split("@").get(0))
        }
        GlobalScope.launch {
            MyPreferences.shared.setIsGoogleLogin(true)
        }
    }

    fun registerUser(context: Context, userName: String, userPassword: String) {
        authentication.registerNewUser(userName, userPassword, context, this)
        auth.currentUser?.sendEmailVerification()
        GlobalScope.launch {
            MyPreferences.shared.setCurrentUserEmail(userName.split("@").get(0))
        }
    }

    fun resetPassword(email: String, context: Context) {
        authentication.resetPassword(email, context)
    }

    fun setUserSuccessfullyLoggedIn() {
        _userLiveData.value = userLiveData.value
    }
}