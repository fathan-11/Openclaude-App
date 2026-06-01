package com.openclaude.android.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.openclaude.android.ui.navigation.BottomNavItem

@Composable
fun BottomNav(
    items: List<BottomNavItem>,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}
