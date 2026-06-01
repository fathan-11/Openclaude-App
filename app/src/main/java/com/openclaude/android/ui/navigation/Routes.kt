package com.openclaude.android.ui.navigation

object Routes {
    const val CHAT = "chat"
    const val CHAT_WITH_ID = "chat/{conversationId}"
    const val CONVERSATIONS = "conversations"
    const val SETTINGS = "settings"

    fun chatWithId(conversationId: String): String = "chat/$conversationId"
}
