package com.openclaude.android.data.remote

import com.openclaude.android.data.remote.dto.ChatCompletionRequest
import com.openclaude.android.data.remote.dto.ChatCompletionResponse
import retrofit2.http.*

interface ApiService {
    @POST("chat/completions")
    suspend fun createChatCompletion(
        @Body request: ChatCompletionRequest,
        @Header("Authorization") authorization: String,
    ): ChatCompletionResponse

    @GET("models")
    suspend fun getModels(
        @Header("Authorization") authorization: String,
    ): ModelsResponse
}

data class ModelsResponse(
    val data: List<ModelDto>
)

data class ModelDto(
    val id: String,
    val `object`: String,
    val created: Long?,
    val owned_by: String?
)
