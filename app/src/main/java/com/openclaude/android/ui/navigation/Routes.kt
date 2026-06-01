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
    const val REPOS = "repos"
    const val PRS = "prs/{owner}/{repo}"
    const val ISSUES = "issues/{owner}/{repo}"
    const val VOICE = "voice"
    const val SKILLS = "skills"
    const val MEMORY = "memory"
    const val TASKS = "tasks"
    const val ABOUT = "about"

    fun chatWithId(conversationId: String): String = "chat/$conversationId"
    fun codeWithPath(path: String): String = "code/$path"
    fun diffWithFile(file: String): String = "diff/$file"
    fun prsWithRepo(owner: String, repo: String): String = "prs/$owner/$repo"
    fun issuesWithRepo(owner: String, repo: String): String = "issues/$owner/$repo"
}
