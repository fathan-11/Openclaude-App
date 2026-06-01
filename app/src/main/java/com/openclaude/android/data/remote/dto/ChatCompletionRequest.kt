package com.openclaude.android.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatCompletionRequest(
    @Json(name = "model") val model: String,
    @Json(name = "messages") val messages: List<MessageDto>,
    @Json(name = "stream") val stream: Boolean = false,
    @Json(name = "temperature") val temperature: Double = 0.7,
    @Json(name = "max_tokens") val maxTokens: Int? = null,
    @Json(name = "top_p") val topP: Double = 1.0,
    @Json(name = "frequency_penalty") val frequencyPenalty: Double = 0.0,
    @Json(name = "presence_penalty") val presencePenalty: Double = 0.0,
)

@JsonClass(generateAdapter = true)
data class MessageDto(
    @Json(name = "role") val role: String,
    @Json(name = "content") val content: String,
)
