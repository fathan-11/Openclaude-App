package com.openclaude.android.data.model

enum class McpServerStatus { DISCONNECTED, CONNECTING, CONNECTED, ERROR }

data class McpServer(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val url: String,
    val description: String = "",
    val status: McpServerStatus = McpServerStatus.DISCONNECTED,
    val tools: List<McpTool> = emptyList(),
    val resources: List<McpResource> = emptyList(),
    val errorMessage: String? = null
)

data class McpTool(
    val name: String,
    val description: String,
    val inputSchema: Map<String, Any> = emptyMap(),
    val serverId: String = ""
)

data class McpResource(
    val uri: String,
    val name: String,
    val mimeType: String = "",
    val description: String = ""
)
