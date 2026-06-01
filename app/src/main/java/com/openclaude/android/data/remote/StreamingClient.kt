package com.openclaude.android.data.remote

import com.openclaude.android.data.remote.dto.ChatCompletionRequest
import com.openclaude.android.data.remote.dto.MessageDto
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class StreamingClient(
    private val moshi: Moshi
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    fun streamChatCompletion(
        baseUrl: String,
        apiKey: String,
        model: String,
        messages: List<MessageDto>,
        temperature: Double = 0.7,
        maxTokens: Int? = null,
    ): Flow<StreamEvent> = callbackFlow {
        val request = ChatCompletionRequest(
            model = model,
            messages = messages,
            stream = true,
            temperature = temperature,
            maxTokens = maxTokens,
        )

        val requestAdapter = moshi.adapter(ChatCompletionRequest::class.java)
        val json = requestAdapter.toJson(request)

        val httpRequest = Request.Builder()
            .url("${baseUrl.trimEnd('/')}/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "text/event-stream")
            .post(json.toRequestBody("application/json".toMediaType()))
            .build()

        val call = client.newCall(httpRequest)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val apiError = when (e) {
                    is SocketTimeoutException -> ApiError.TimeoutError("Connection timed out")
                    else -> ApiError.NetworkError(e.message ?: "Network error")
                }
                trySend(StreamEvent.Error(apiError.userMessage))
                close()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    val apiError = ApiError.fromHttpCode(response.code, response.message)
                    trySend(StreamEvent.Error(apiError.userMessage))
                    close()
                    return
                }

                try {
                    val reader = BufferedReader(InputStreamReader(response.body!!.byteStream()))
                    val streamAdapter = moshi.adapter(
                        com.openclaude.android.data.remote.dto.StreamChunkDto::class.java
                    )

                    var line: String?
                    var emptyChunkCount = 0
                    val maxEmptyChunks = 100

                    while (reader.readLine().also { line = it } != null) {
                        val currentLine = line ?: continue
                        
                        if (currentLine.isBlank()) continue
                        
                        if (currentLine.startsWith("data: ")) {
                            val data = currentLine.removePrefix("data: ").trim()
                            
                            if (data == "[DONE]") {
                                trySend(StreamEvent.Done)
                                break
                            }
                            
                            if (data.isEmpty()) {
                                emptyChunkCount++
                                if (emptyChunkCount >= maxEmptyChunks) {
                                    trySend(StreamEvent.Error("Too many empty response chunks received."))
                                    break
                                }
                                continue
                            }
                            
                            emptyChunkCount = 0

                            try {
                                val chunk = streamAdapter.fromJson(data)
                                val content = chunk?.choices?.firstOrNull()?.delta?.content
                                
                                if (!content.isNullOrEmpty()) {
                                    trySend(StreamEvent.Content(content))
                                }
                                
                                val finishReason = chunk?.choices?.firstOrNull()?.finishReason
                                if (finishReason != null) {
                                    trySend(StreamEvent.Done)
                                    break
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                continue
                            }
                        }
                    }
                    reader.close()
                } catch (e: SocketTimeoutException) {
                    trySend(StreamEvent.Error(ApiError.TimeoutError("Connection timed out during streaming").userMessage))
                } catch (e: IOException) {
                    trySend(StreamEvent.Error(ApiError.NetworkError("Connection lost during streaming: ${e.message}").userMessage))
                } catch (e: Exception) {
                    trySend(StreamEvent.Error(ApiError.UnknownError(e.message ?: "Stream error").userMessage))
                } finally {
                    response.close()
                    close()
                }
            }
        })

        awaitClose { call.cancel() }
    }

    suspend fun getModels(
        baseUrl: String,
        apiKey: String,
    ): List<String> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${baseUrl.trimEnd('/')}/models")
            .addHeader("Authorization", "Bearer $apiKey")
            .get()
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            val apiError = ApiError.fromHttpCode(response.code, response.message)
            throw apiError.toException()
        }

        val body = response.body?.string() ?: throw ApiError.UnknownError("Empty response").toException()
        val moshiAdapter = moshi.adapter(ModelsListResponse::class.java)
        val modelsResponse = moshiAdapter.fromJson(body) ?: throw ApiError.UnknownError("Parse error").toException()
        modelsResponse.data.map { it.id }
    }
}

/**
 * Extension to convert ApiError to Exception for throwing.
 */
fun ApiError.toException(): Exception = Exception(this.userMessage)

sealed class StreamEvent {
    data class Content(val text: String) : StreamEvent()
    data class Error(val message: String) : StreamEvent()
    data object Done : StreamEvent()
}

data class ModelsListResponse(
    val data: List<ModelItem>
)

data class ModelItem(
    val id: String
)
