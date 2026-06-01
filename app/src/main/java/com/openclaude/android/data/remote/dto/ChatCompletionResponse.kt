package com.openclaude.android.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatCompletionResponse(
    @Json(name = "id") val id: String,
    @Json(name = "object") val objectType: String,
    @Json(name = "created") val created: Long,
    @Json(name = "model") val model: String,
    @Json(name = "choices") val choices: List<ChoiceDto>,
    @Json(name = "usage") val usage: UsageDto? = null,
)

@JsonClass(generateAdapter = true)
data class ChoiceDto(
    @Json(name = "index") val index: Int,
    @Json(name = "message") val message: MessageDto? = null,
    @Json(name = "delta") val delta: DeltaDto? = null,
    @Json(name = "finish_reason") val finishReason: String? = null,
)

@JsonClass(generateAdapter = true)
data class DeltaDto(
    @Json(name = "role") val role: String? = null,
    @Json(name = "content") val content: String? = null,
)

@JsonClass(generateAdapter = true)
data class UsageDto(
    @Json(name = "prompt_tokens") val promptTokens: Int,
    @Json(name = "completion_tokens") val completionTokens: Int,
    @Json(name = "total_tokens") val totalTokens: Int,
)

// For SSE streaming parsing
@JsonClass(generateAdapter = true)
data class StreamChunkDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "object") val objectType: String? = null,
    @Json(name = "choices") val choices: List<StreamChoiceDto>? = null,
)

@JsonClass(generateAdapter = true)
data class StreamChoiceDto(
    @Json(name = "index") val index: Int? = null,
    @Json(name = "delta") val delta: StreamDeltaDto? = null,
    @Json(name = "finish_reason") val finishReason: String? = null,
)

@JsonClass(generateAdapter = true)
data class StreamDeltaDto(
    @Json(name = "content") val content: String? = null,
    @Json(name = "role") val role: String? = null,
)
