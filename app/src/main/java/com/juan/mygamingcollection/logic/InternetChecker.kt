package com.juan.mygamingcollection.logic

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

object InternetChecker {
    //This is better to have it checked in a BroadcastReceiver so we can have automatically when theres a change of connectivity.
    fun isNetworkAvailable(context: Context) : Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnected == true
    }
}