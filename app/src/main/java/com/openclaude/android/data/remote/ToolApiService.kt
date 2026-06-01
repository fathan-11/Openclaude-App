package com.openclaude.android.data.remote

import com.openclaude.android.data.model.*
import retrofit2.http.*

interface ToolApiService {
    @POST("tools/execute")
    suspend fun executeTool(@Body execution: ToolExecution): ToolResult

    @GET("tools/status/{id}")
    suspend fun getStatus(@Path("id") executionId: String): ToolExecution

    @GET("tools/history")
    suspend fun getHistory(@Query("limit") limit: Int = 50): List<ToolExecution>
}
