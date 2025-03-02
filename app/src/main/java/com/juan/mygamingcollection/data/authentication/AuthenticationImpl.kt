package com.juan.mygamingcollection.data.authentication

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.juan.mygamingcollection.R
import com.juan.mygamingcollection.data.preferences.MyPreferences
import com.juan.mygamingcollection.data.viewmodel.ScreenViewModel
import com.juan.mygamingcollection.data.viewmodel.UserViewModel
import com.juan.mygamingcollection.ui.screens.keyboardController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AuthenticationImpl : IAuthentication {

    var auth = Firebase.auth
    var user:FirebaseUser? = auth.currentUser

    override fun checkUserIsLoggedIn() : Boolean{
        if (auth.currentUser != null)
            return true
        return false
    }

    override fun attemptToLogin(userViewModel: UserViewModel, userName: String, userPassword: String, context: Context, screenViewModel: ScreenViewModel) : Int{
        var responseCode = 0
        if (getCurrentConnectivityState(context)) {
            auth.signInWithEmailAndPassword(userName, userPassword)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        responseCode = 0
                        Toast.makeText(context, context.getText(R.string.authentication_failed), Toast.LENGTH_SHORT).show()
                    } else {
                        keyboardController.hide()
                        userViewModel.setUserSuccessfullyLoggedIn()
                        responseCode = 1
                        GlobalScope.launch {
                            MyPreferences.shared.setIsGoogleLogin(false)
                        }
                    }
                }.addOnCanceledListener {
                    responseCode = 0
                    Toast.makeText(context, context.getText(R.string.authentication_failed), Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    responseCode = 0
                    Toast.makeText(context, context.getText(R.string.authentication_failed), Toast.LENGTH_SHORT).show()
                }
        }
        else {
            responseCode = 0
            Toast.makeText(context, context.resources.getText(R.string.no_network_available), Toast.LENGTH_SHORT).show()
        }
        return responseCode
    }

    override fun registerUserWithGoogle(
        googleIdTokenCredential: GoogleIdTokenCredential,
        userViewModel: UserViewModel,
        context: Context,
        userName: String
    ) {
        val idToken = googleIdTokenCredential.idToken
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(firebaseCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, context.getText(R.string.authentication_succeed), Toast.LENGTH_SHORT).show()
                userViewModel.setUser(task.result.user?.email?.split("@")!!.get(0))
                keyboardController.hide()
                GlobalScope.launch(Dispatchers.Default) {
                    MyPreferences.shared.setCurrentUserEmail(userName.split("@").get(0))
                    MyPreferences.shared.setIsGoogleLogin(true)
                }
            }
        }.addOnCanceledListener {
            Toast.makeText(context, context.getText(R.string.registration_failed), Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
                Toast.makeText(context, context.getText(R.string.registration_failed), Toast.LENGTH_SHORT).show()
        }
    }

    override fun resetPassword(email: String, context: Context) {
        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, context.getText(R.string.email_reset_text), Toast.LENGTH_LONG).show()
                    keyboardController.hide()
                    GlobalScope.launch {
                        MyPreferences.shared.setIsGoogleLogin(false)
                    }
                }
                else {
                    Toast.makeText(context, context.getText(R.string.failed_to_update_password), Toast.LENGTH_LONG).show()
                    keyboardController.hide()
                }
            }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun registerNewUser(userName: String, userPassword: String, context: Context, userViewModel: UserViewModel){
        if (getCurrentConnectivityState(context)) {
            auth.createUserWithEmailAndPassword(userName, userPassword)
                .addOnCompleteListener {
                    if (it.isSuccessful)
                        userViewModel.setUser(userName)
                        GlobalScope.launch {
                            MyPreferences.shared.setCurrentUserEmail(userName.split("@").get(0))
                        }
                        Toast.makeText(context, context.getText(R.string.authentication_succeed), Toast.LENGTH_SHORT).show()
                        keyboardController.hide()
                        GlobalScope.launch {
                            MyPreferences.shared.setIsGoogleLogin(false)
                        }
                    }.addOnCanceledListener {
                    Toast.makeText(context, context.getText(R.string.registration_failed), Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(context, context.getText(R.string.registration_failed), Toast.LENGTH_SHORT).show()
                }
        }
        else
            Toast.makeText(context, context.resources.getText(R.string.no_network_available), Toast.LENGTH_SHORT).show()
    }

    override fun logout() {
        Firebase.auth.signOut()
    }

    fun getCurrentConnectivityState(context: Context) : Boolean {
        val connectivityManager = (context.getSystemService(Context.CONNECTIVITY_SERVICE)) as ConnectivityManager
        val connected = connectivityManager.allNetworks.any { network ->
            connectivityManager.getNetworkCapabilities(network)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                ?: false
        }
        return if (connected) true else false
    }
}