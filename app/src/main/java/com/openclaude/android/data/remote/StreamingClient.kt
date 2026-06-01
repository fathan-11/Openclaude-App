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
                val errorMessage = when (e) {
                    is SocketTimeoutException -> "Connection timed out. Please check your network and try again."
                    else -> e.message ?: "Unknown error"
                }
                trySend(StreamEvent.Error(errorMessage))
                close()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    val errorMessage = when (response.code) {
                        401 -> "Invalid API key. Please check your settings."
                        403 -> "Access denied. Please verify your API key permissions."
                        429 -> "Rate limit exceeded. Please wait and try again."
                        500, 502, 503 -> "Server error (${response.code}). Please try again later."
                        else -> "HTTP ${response.code}: ${response.message}"
                    }
                    trySend(StreamEvent.Error(errorMessage))
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
                    val maxEmptyChunks = 100 // Prevent infinite loops on empty data

                    while (reader.readLine().also { line = it } != null) {
                        val currentLine = line ?: continue
                        
                        // Skip empty lines (SSE format uses empty lines as delimiters)
                        if (currentLine.isBlank()) continue
                        
                        if (currentLine.startsWith("data: ")) {
                            val data = currentLine.removePrefix("data: ").trim()
                            
                            // Handle [DONE] signal
                            if (data == "[DONE]") {
                                trySend(StreamEvent.Done)
                                break
                            }
                            
                            // Skip empty data chunks
                            if (data.isEmpty()) {
                                emptyChunkCount++
                                if (emptyChunkCount >= maxEmptyChunks) {
                                    trySend(StreamEvent.Error("Too many empty response chunks received."))
                                    break
                                }
                                continue
                            }
                            
                            emptyChunkCount = 0 // Reset counter on valid data

                            try {
                                val chunk = streamAdapter.fromJson(data)
                                val content = chunk?.choices?.firstOrNull()?.delta?.content
                                
                                // Filter out empty content chunks
                                if (!content.isNullOrEmpty()) {
                                    trySend(StreamEvent.Content(content))
                                }
                                
                                val finishReason = chunk?.choices?.firstOrNull()?.finishReason
                                if (finishReason != null) {
                                    trySend(StreamEvent.Done)
                                    break
                                }
                            } catch (e: Exception) {
                                // Log malformed JSON but continue processing
                                // This handles edge cases where SSE events may have malformed JSON
                                e.printStackTrace()
                                continue
                            }
                        }
                    }
                    reader.close()
                } catch (e: SocketTimeoutException) {
                    trySend(StreamEvent.Error("Connection timed out during streaming. Response may be incomplete."))
                } catch (e: IOException) {
                    trySend(StreamEvent.Error("Connection lost during streaming: ${e.message}"))
                } catch (e: Exception) {
                    trySend(StreamEvent.Error("Stream error: ${e.message}"))
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
            throw IOException("HTTP ${response.code}: ${response.message}")
        }

        val body = response.body?.string() ?: throw IOException("Empty response")
        val moshiAdapter = moshi.adapter(ModelsListResponse::class.java)
        val modelsResponse = moshiAdapter.fromJson(body) ?: throw IOException("Parse error")
        modelsResponse.data.map { it.id }
    }
}

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
