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
import com.juan.mygamingcollection.data.viewmodel.UserViewModel
import com.juan.mygamingcollection.model.User

object AuthenticationImpl : IAuthentication {

    val auth = Firebase.auth
    var user:FirebaseUser? = null

    override fun checkUserIsLoggedIn() : Boolean{
        if (auth.currentUser != null)
            return true
        return false
    }

    override fun attemptToLogin(user: User, context: Context) : User {
        var returnUser = User("","", false)
        if (getCurrentConnectivityState(context)) {
            auth.signInWithEmailAndPassword(user.name, user.password)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(context, context.getText(R.string.authentication_failed), Toast.LENGTH_SHORT).show()
                    }
                    else
                        returnUser = user
                }
        }
        else
            Toast.makeText(context, context.resources.getText(R.string.no_network_available), Toast.LENGTH_SHORT).show()

        return returnUser
    }

    override fun registerUserWithGoogle(
        googleIdTokenCredential: GoogleIdTokenCredential,
        userViewModel: UserViewModel
    ) {
        val idToken = googleIdTokenCredential.idToken
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(firebaseCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                userViewModel.setUserData(auth.currentUser?.email.let { User(it ?:"", idToken, true) })//auth.currentUser.))
            }
        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun registerNewUser(user: User, context: Context) : User{
        var returnUser = User("","", false)
        if (getCurrentConnectivityState(context)) {
            auth.createUserWithEmailAndPassword(user.name, user.password)
                .addOnCompleteListener {
                    if (it.isSuccessful)
                        Toast.makeText(context, context.getText(R.string.authentication_succeed), Toast.LENGTH_SHORT).show()
                        returnUser = user
                    }
        }
        else
            Toast.makeText(context, context.resources.getText(R.string.no_network_available), Toast.LENGTH_SHORT).show()
        return returnUser
    }

    override fun logout(user: User) {
//        When a user signs out of your app, call the API clearCredentialState() method to clear the current user credential state and reset the internal state of sign-in.
    }

    private fun getCurrentConnectivityState(context: Context) : Boolean {
        val connectivityManager = (context.getSystemService(Context.CONNECTIVITY_SERVICE)) as ConnectivityManager
        val connected = connectivityManager.allNetworks.any { network ->
            connectivityManager.getNetworkCapabilities(network)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                ?: false
        }
        return if (connected) true else false
    }

    /*
    //Check user is signed in
    val user = Firebase.auth.currentUser
    if (user != null)
    Log.i("LOGIN", "User has signed in")
    else
    Log.i("LOGIN", "User hasnt signed in")
    //get user's data
    //user.displayName(),..., .providerData() gives us more detaild contents

    //update users data
    val profileUpdates = userProfileChangeRequest {
        displayName = "Jane Q. User"
        photoUri = Uri.parse("https://example.com/jane-q-user/profile.jpg")
    }

    user!!.updateProfile(profileUpdates)
    .addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Log.d(TAG, "User profile updated.")
        }
    }

    //deleta user
    user.delete()
    .addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Log.d(TAG, "User account deleted.")
        }
    }

    //register


    //---login--///
    p.177 FUREBASE COOKBOOK

    //Logout
    //use .signOut()
    */
}