package com.openclaude.android.domain.usecase

import com.openclaude.android.data.model.*
import com.openclaude.android.data.repository.McpRepository
import javax.inject.Inject

class ManageMcpUseCase @Inject constructor(
    private val mcpRepository: McpRepository
) {
    suspend fun loadServers(): Result<List<McpServer>> = mcpRepository.loadServers()
    suspend fun addServer(name: String, url: String, description: String = ""): Result<McpServer> = mcpRepository.addServer(name, url, description)
    suspend fun removeServer(serverId: String): Result<Unit> = mcpRepository.removeServer(serverId)
    suspend fun connect(serverId: String): Result<McpServer> = mcpRepository.connectServer(serverId)
    suspend fun disconnect(serverId: String): Result<Unit> = mcpRepository.disconnectServer(serverId)
    suspend fun executeTool(toolName: String, input: Map<String, Any>): Result<ToolResult> = mcpRepository.executeTool(toolName, input)
}
