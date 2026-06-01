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
        }
    }
}
