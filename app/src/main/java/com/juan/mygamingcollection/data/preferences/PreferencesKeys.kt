package com.juan.mygamingcollection.data.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val IS_GOOGLE_LOGIN ="is_google_login"
    val CURRENT_USER_EMAIL = "current_user_email"
    val googleLogin = booleanPreferencesKey(IS_GOOGLE_LOGIN)
    val currentUserEmail = stringPreferencesKey(CURRENT_USER_EMAIL)
}