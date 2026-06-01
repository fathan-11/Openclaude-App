package com.openclaude.android.ui.navigation

object Routes {
    const val CHAT = "chat"
    const val CHAT_WITH_ID = "chat/{conversationId}"
    const val CONVERSATIONS = "conversations"
    const val SETTINGS = "settings"
    const val FILES = "files"
    const val CODE = "code/{path}"
    const val SEARCH = "search"
    const val DIFF = "diff/{file}"
    const val TERMINAL = "terminal"
    const val TOOLS = "tools"
    const val MCP = "mcp"

    fun chatWithId(conversationId: String): String = "chat/$conversationId"
    fun codeWithPath(path: String): String = "code/$path"
    fun diffWithFile(file: String): String = "diff/$file"
}
