package com.openclaude.android.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.openclaude.android.ui.screens.chat.ChatScreen
import com.openclaude.android.ui.screens.conversations.ConversationListScreen
import com.openclaude.android.ui.screens.settings.SettingsScreen
import com.openclaude.android.ui.screens.files.FileBrowserScreen
import com.openclaude.android.ui.screens.codeviewer.CodeViewerScreen
import com.openclaude.android.ui.screens.search.SearchScreen
import com.openclaude.android.ui.screens.diff.DiffViewerScreen
import com.openclaude.android.ui.screens.terminal.TerminalScreen
import com.openclaude.android.ui.screens.tools.ToolOutputScreen
import com.openclaude.android.ui.screens.mcp.McpScreen
import com.openclaude.android.data.model.DiffResult
import com.openclaude.android.data.model.DiffHunk
import com.openclaude.android.ui.components.BottomNav
import com.openclaude.android.ui.components.TopBar

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String,
)

val bottomNavItems = listOf(
    BottomNavItem("Chat", Icons.Default.Chat, Routes.CHAT),
    BottomNavItem("History", Icons.Default.History, Routes.CONVERSATIONS),
    BottomNavItem("Files", Icons.Default.Folder, Routes.FILES),
    BottomNavItem("Search", Icons.Default.Search, Routes.SEARCH),
    BottomNavItem("Terminal", Icons.Default.Terminal, Routes.TERMINAL),
    BottomNavItem("Tools", Icons.Default.Build, Routes.TOOLS),
    BottomNavItem("MCP", Icons.Default.Hub, Routes.MCP),
    BottomNavItem("Settings", Icons.Default.Settings, Routes.SETTINGS),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.hierarchy?.any { dest ->
        bottomNavItems.any { it.route == dest.route }
    } == true

    Scaffold(
        topBar = {
            if (showBottomBar) {
                TopBar(title = "OpenClaude")
            }
        },
        bottomBar = {
            if (showBottomBar) {
                BottomNav(
                    items = bottomNavItems,
                    currentRoute = currentDestination?.route ?: Routes.CHAT,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.CHAT,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.CHAT) {
                ChatScreen(
                    conversationId = null,
                    onNavigateToConversation = { id ->
                        navController.navigate(Routes.chatWithId(id))
                    }
                )
            }
            composable(
                Routes.CHAT_WITH_ID,
                arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
            ) { backStackEntry ->
                val conversationId = backStackEntry.arguments?.getString("conversationId")
                ChatScreen(
                    conversationId = conversationId,
                    onNavigateToConversation = { }
                )
            }
            composable(Routes.CONVERSATIONS) {
                ConversationListScreen(
                    onConversationClick = { id ->
                        navController.navigate(Routes.chatWithId(id))
                    }
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen()
            }
            composable(Routes.FILES) {
                FileBrowserScreen(
                    onFileClick = { path ->
                        navController.navigate(Routes.codeWithPath(path))
                    }
                )
            }
            composable(
                Routes.CODE,
                arguments = listOf(navArgument("path") { type = NavType.StringType })
            ) { backStackEntry ->
                val path = backStackEntry.arguments?.getString("path") ?: ""
                CodeViewerScreen(
                    filePath = path,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.SEARCH) {
                SearchScreen(
                    onResultClick = { path ->
                        navController.navigate(Routes.codeWithPath(path))
                    }
                )
            }
            composable(Routes.TERMINAL) {
                TerminalScreen()
            }
            composable(Routes.TOOLS) {
                ToolOutputScreen()
            }
            composable(Routes.MCP) {
                McpScreen()
            }
            composable(
                Routes.DIFF,
                arguments = listOf(navArgument("file") { type = NavType.StringType })
            ) { backStackEntry ->
                val file = backStackEntry.arguments?.getString("file") ?: ""
                // DiffViewerScreen requires a DiffResult - this would need to be loaded via ViewModel
                // For now, placeholder that navigates back
                // In a full implementation, use a DiffViewerViewModel to load the diff
            }
        }
    }
}
