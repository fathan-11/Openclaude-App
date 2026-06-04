package com.openclaude.android.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Monitors network connectivity using ConnectivityManager and exposes
 * the current [NetworkState] as a cold Flow.
 */
@Singleton
class NetworkConnectivityMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    /**
     * Observe the current network state. Emits a new [NetworkState] whenever
     * connectivity changes. The initial value is determined synchronously.
     */
    fun observe(): Flow<NetworkState> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(NetworkState.Connected)
            }

            override fun onLost(network: Network) {
                trySend(NetworkState.Disconnected)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                val isValidated = networkCapabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_VALIDATED
                )
                val hasInternet = networkCapabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                )
                when {
                    isValidated -> trySend(NetworkState.Connected)
                    hasInternet -> trySend(NetworkState.Limited)
                    else -> trySend(NetworkState.Disconnected)
                }
            }

            override fun onUnavailable() {
                trySend(NetworkState.Disconnected)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Emit the current state immediately
        trySend(getCurrentState())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    /**
     * Synchronously check whether the device currently has network connectivity.
     */
    fun isConnected(): Boolean {
        return getCurrentState() is NetworkState.Connected
    }

    private fun getCurrentState(): NetworkState {
        val network = connectivityManager.activeNetwork ?: return NetworkState.Disconnected
        val capabilities = connectivityManager.getNetworkCapabilities(network)
            ?: return NetworkState.Disconnected

        return when {
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) ->
                NetworkState.Connected
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ->
                NetworkState.Limited
            else -> NetworkState.Disconnected
        }
    }
}
