package io.github.kuroka3.mealapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities

object StatusManager {
    var alarmPerm: Boolean = false

    fun checkNetworkState(context: Context): Boolean {
        val connectivityManager: ConnectivityManager =
            context.getSystemService(ConnectivityManager::class.java)
        val network: Network = connectivityManager.activeNetwork ?: return false
        val actNetwork: NetworkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            else -> false
        }
    }
}