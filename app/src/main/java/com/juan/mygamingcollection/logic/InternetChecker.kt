package com.juan.mygamingcollection.logic

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

object InternetChecker {
    fun isNetworkAvailable(context: Context) : Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnected == true
    }
}