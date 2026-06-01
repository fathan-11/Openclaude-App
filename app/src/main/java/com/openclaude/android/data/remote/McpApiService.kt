package com.openclaude.android.data.remote

import com.openclaude.android.data.model.*
import retrofit2.http.*

interface McpApiService {
    @GET("mcp/servers")
    suspend fun getServers(): List<McpServer>

    @POST("mcp/servers")
    suspend fun addServer(@Body server: McpServer): McpServer

    @DELETE("mcp/servers/{id}")
    suspend fun removeServer(@Path("id") serverId: String)

    @POST("mcp/servers/{id}/connect")
    suspend fun connectServer(@Path("id") serverId: String): McpServer

    @POST("mcp/servers/{id}/disconnect")
    suspend fun disconnectServer(@Path("id") serverId: String)

    @POST("mcp/tools/{name}/execute")
    suspend fun executeTool(@Path("name") toolName: String, @Body input: Map<String, Any>): ToolResult

    @GET("mcp/servers/{id}/resources")
    suspend fun getResources(@Path("id") serverId: String): List<McpResource>
}
