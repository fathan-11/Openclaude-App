package com.openclaude.android.data.network

/**
 * Represents the current network connectivity state.
 */
sealed class NetworkState {
    /** Network is connected and fully available. */
    data object Connected : NetworkState()

    /** Network is disconnected — no connectivity. */
    data object Disconnected : NetworkState()

    /** Network is connected but has limited capabilities (e.g., captive portal). */
    data object Limited : NetworkState()
}
