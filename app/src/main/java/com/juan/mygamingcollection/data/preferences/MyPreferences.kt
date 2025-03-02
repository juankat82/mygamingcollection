package com.juan.mygamingcollection.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MyPreferences {
    companion object {
        val shared = MyPreferences()
    }
    var myContext: Context? = null
    private val USER_PREFERENCES_NAME = "user_preferences"
    val Context.dataStore by preferencesDataStore(USER_PREFERENCES_NAME)

    fun initialize(context: Context) {
        myContext = context
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getIsGoogleLogin() : Boolean {
        var isGoogleLogin = false
        runBlocking {
            isGoogleLogin = GlobalScope.async { myContext?.dataStore?.data?.firstOrNull()?.get(PreferencesKeys.googleLogin) ?: false}.await()
        }
        return isGoogleLogin
    }

    /*suspend */fun setIsGoogleLogin(isGoogleLogin: Boolean) : Boolean{
        GlobalScope.launch {
            myContext?.dataStore?.edit { preferences ->
                preferences[PreferencesKeys.googleLogin] = isGoogleLogin
            }
        }
        return true
    }

    /*suspend */fun setCurrentUserEmail(currentUserEmail: String) : Boolean {
        GlobalScope.launch {
            myContext?.dataStore?.edit { preferences ->
                preferences[PreferencesKeys.currentUserEmail] = currentUserEmail
            }
        }
        return true
    }

    suspend fun getCurrentUserEmail() : String {
        val currentUserEmail = myContext?.dataStore?.data?.firstOrNull()?.get(PreferencesKeys.currentUserEmail) ?: ""
        return currentUserEmail
    }
}