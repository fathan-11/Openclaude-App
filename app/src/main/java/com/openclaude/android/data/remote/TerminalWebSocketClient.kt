package com.openclaude.android.data.remote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class TerminalStreamMessage(
    val type: String,
    val sessionId: String,
    val data: String,
    val exitCode: Int? = null
)

@Singleton
class TerminalWebSocketClient @Inject constructor() {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .pingInterval(30, TimeUnit.SECONDS)
        .build()

    private val _messages = MutableSharedFlow<TerminalStreamMessage>(replay = 1)
    val messages: Flow<TerminalStreamMessage> = _messages.asSharedFlow()

    private val _connectionState = MutableSharedFlow<Boolean>(replay = 1)
    val connectionState: Flow<Boolean> = _connectionState.asSharedFlow()

    fun connect(baseUrl: String, sessionId: String) {
        val url = baseUrl.replace("http", "ws") + "/terminal/ws/$sessionId"
        val request = Request.Builder().url(url).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                _connectionState.tryEmit(true)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val message = Json.decodeFromString<TerminalStreamMessage>(text)
                    _messages.tryEmit(message)
                } catch (e: Exception) {
                    _messages.tryEmit(TerminalStreamMessage("output", sessionId, text))
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
                _connectionState.tryEmit(false)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                _connectionState.tryEmit(false)
            }
        })
    }

    fun sendCommand(command: String) {
        webSocket?.send("""{"type":"command","data":"$command"}""")
    }

    fun sendInput(data: String) {
        webSocket?.send("""{"type":"input","data":"$data"}""")
    }

    fun resize(cols: Int, rows: Int) {
        webSocket?.send("""{"type":"resize","cols":$cols,"rows":$rows}""")
    }

    fun disconnect() {
        webSocket?.close(1000, "Client disconnect")
        webSocket = null
        _connectionState.tryEmit(false)
    }
}
