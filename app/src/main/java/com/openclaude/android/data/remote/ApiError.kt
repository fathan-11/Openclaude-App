package com.openclaude.android.data.remote

/**
 * Sealed class representing different types of API errors.
 * Provides structured error handling for all API operations.
 */
sealed class ApiError(val message: String, val code: Int? = null) {
    
    /** Network connectivity issues */
    class NetworkError(message: String) : ApiError(message)
    
    /** Request timed out */
    class TimeoutError(message: String) : ApiError(message)
    
    /** Authentication/authorization errors (401, 403) */
    class AuthError(message: String, code: Int? = null) : ApiError(message, code)
    
    /** Server-side errors (5xx) */
    class ServerError(message: String, code: Int? = null) : ApiError(message, code)
    
    /** Rate limiting (429) */
    class RateLimitError(message: String, val retryAfter: Long? = null) : ApiError(message, 429)
    
    /** Client-side errors (4xx except 401, 403, 429) */
    class ClientError(message: String, code: Int? = null) : ApiError(message, code)
    
    /** Unknown/unexpected errors */
    class UnknownError(message: String) : ApiError(message)

    /**
     * Returns true if this error is transient and can be retried.
     */
    val isRetryable: Boolean
        get() = when (this) {
            is NetworkError -> true
            is TimeoutError -> true
            is ServerError -> true
            is RateLimitError -> true
            else -> false
        }

    /**
     * Returns a user-friendly error message.
     */
    val userMessage: String
        get() = when (this) {
            is NetworkError -> "Network error. Please check your internet connection and try again."
            is TimeoutError -> "Request timed out. Please try again."
            is AuthError -> "Authentication failed. Please check your API key in settings."
            is ServerError -> "Server error (${code ?: "unknown"}). Please try again later."
            is RateLimitError -> "Rate limit exceeded. Please wait a moment and try again."
            is ClientError -> "Request error: $message"
            is UnknownError -> "An unexpected error occurred: $message"
        }

    companion object {
        /**
         * Maps an exception to an appropriate ApiError.
         */
        fun fromException(e: Exception): ApiError {
            return when {
                e.message?.contains("timeout", ignoreCase = true) == true ||
                e.message?.contains("timed out", ignoreCase = true) == true -> {
                    TimeoutError(e.message ?: "Request timed out")
                }
                e.message?.contains("network", ignoreCase = true) == true ||
                e.message?.contains("connect", ignoreCase = true) == true ||
                e.message?.contains("socket", ignoreCase = true) == true -> {
                    NetworkError(e.message ?: "Network error")
                }
                else -> UnknownError(e.message ?: "Unknown error")
            }
        }

        /**
         * Maps an HTTP status code to an appropriate ApiError.
         */
        fun fromHttpCode(code: Int, message: String): ApiError {
            return when (code) {
                401, 403 -> AuthError(message, code)
                429 -> RateLimitError(message)
                in 500..599 -> ServerError(message, code)
                in 400..499 -> ClientError(message, code)
                else -> UnknownError("HTTP $code: $message")
            }
        }
    }
}
