package com.openclaude.android.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openclaude.android.core.ui.theme.*
import com.openclaude.android.ui.navigation.BottomNavItem

// ═══════════════════════════════════════════════════════════════
// LINEAR-STYLE BOTTOM NAVIGATION
// Ultra-minimal, dark, precision-engineered
// ═══════════════════════════════════════════════════════════════

@Composable
fun BottomNav(
    items: List<BottomNavItem>,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = PanelDark,
        contentColor = TextPrimary,
        modifier = Modifier
            .background(PanelDark)
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            val iconColor by animateColorAsState(
                targetValue = if (isSelected) AccentViolet else TextQuaternary,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "icon_color"
            )

            val textColor by animateColorAsState(
                targetValue = if (isSelected) AccentViolet else TextQuaternary,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "text_color"
            )

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                        color = textColor
                    )
                },
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
