package com.openclaude.android.data.repository

import com.openclaude.android.data.model.*
import com.openclaude.android.data.remote.McpApiService
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class McpRepository @Inject constructor(
    private val mcpApiService: McpApiService
) {
    private val _servers = MutableStateFlow<List<McpServer>>(emptyList())
    val servers: StateFlow<List<McpServer>> = _servers.asStateFlow()

    suspend fun loadServers(): Result<List<McpServer>> {
        return try {
            val servers = mcpApiService.getServers()
            _servers.value = servers
            Result.success(servers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addServer(name: String, url: String, description: String = ""): Result<McpServer> {
        return try {
            val server = McpServer(name = name, url = url, description = description)
            val created = mcpApiService.addServer(server)
            _servers.value = _servers.value + created
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeServer(serverId: String): Result<Unit> {
        return try {
            mcpApiService.removeServer(serverId)
            _servers.value = _servers.value.filter { it.id != serverId }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun connectServer(serverId: String): Result<McpServer> {
        return try {
            _servers.value = _servers.value.map {
                if (it.id == serverId) it.copy(status = McpServerStatus.CONNECTING) else it
            }
            val server = mcpApiService.connectServer(serverId)
            _servers.value = _servers.value.map {
                if (it.id == serverId) server else it
            }
            Result.success(server)
        } catch (e: Exception) {
            _servers.value = _servers.value.map {
                if (it.id == serverId) it.copy(status = McpServerStatus.ERROR, errorMessage = e.message) else it
            }
            Result.failure(e)
        }
    }

    suspend fun disconnectServer(serverId: String): Result<Unit> {
        return try {
            mcpApiService.disconnectServer(serverId)
            _servers.value = _servers.value.map {
                if (it.id == serverId) it.copy(status = McpServerStatus.DISCONNECTED) else it
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private val _tools = MutableStateFlow<List<McpTool>>(emptyList())
    val tools: StateFlow<List<McpTool>> = _tools.asStateFlow()

    suspend fun registerTools(serverId: String, tools: List<McpTool>) {
        _tools.value = _tools.value.filter { it.serverId != serverId } + tools
    }

    suspend fun executeTool(toolName: String, input: Map<String, Any>): Result<ToolResult> {
        return try {
            val result = mcpApiService.executeTool(toolName, input)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
