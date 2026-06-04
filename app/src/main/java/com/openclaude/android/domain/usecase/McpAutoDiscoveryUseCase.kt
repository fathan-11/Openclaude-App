package com.openclaude.android.domain.usecase

import com.openclaude.android.data.model.McpServer
import com.openclaude.android.data.model.McpTool
import com.openclaude.android.data.remote.McpApiService
import com.openclaude.android.data.repository.McpRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class McpAutoDiscoveryUseCase @Inject constructor(
    private val mcpRepository: McpRepository,
    private val mcpApiService: McpApiService
) {
    private val _discoveredTools = MutableStateFlow<List<McpTool>>(emptyList())
    val discoveredTools = _discoveredTools.asStateFlow()

    private val _discoveryState = MutableStateFlow<DiscoveryState>(DiscoveryState.Idle)
    val discoveryState = _discoveryState.asStateFlow()

    sealed class DiscoveryState {
        object Idle : DiscoveryState()
        object Discovering : DiscoveryState()
        data class Found(val tools: List<McpTool>) : DiscoveryState()
        data class Error(val message: String) : DiscoveryState()
    }

    suspend fun discoverTools(server: McpServer): Result<List<McpTool>> {
        _discoveryState.value = DiscoveryState.Discovering
        return try {
            val tools = mcpApiService.listTools(server.id)
            _discoveredTools.value = tools
            _discoveryState.value = DiscoveryState.Found(tools)
            mcpRepository.registerTools(server.id, tools)
            Result.success(tools)
        } catch (e: Exception) {
            _discoveryState.value = DiscoveryState.Error(e.message ?: "Discovery failed")
            Result.failure(e)
        }
    }

    suspend fun discoverFromAllServers(servers: List<McpServer>): Result<List<McpTool>> {
        _discoveryState.value = DiscoveryState.Discovering
        val allTools = mutableListOf<McpTool>()

        for (server in servers) {
            try {
                val tools = mcpApiService.listTools(server.id)
                allTools.addAll(tools)
                mcpRepository.registerTools(server.id, tools)
            } catch (e: Exception) {
                // Continue with other servers
            }
        }

        _discoveredTools.value = allTools
        _discoveryState.value = if (allTools.isNotEmpty()) {
            DiscoveryState.Found(allTools)
        } else {
            DiscoveryState.Error("No tools discovered")
        }

        return Result.success(allTools)
    }

    fun getToolsForServer(serverId: String): List<McpTool> {
        return _discoveredTools.value.filter { it.serverId == serverId }
    }

    fun searchTools(query: String): List<McpTool> {
        return _discoveredTools.value.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.description.contains(query, ignoreCase = true)
        }
    }
}
