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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openclaude.android.core.ui.theme.*
import com.openclaude.android.ui.navigation.BottomNavItem

// ═══════════════════════════════════════════════════════════════
// DARK MODE + ORANGE BOTTOM NAVIGATION
// Clean dark bar with orange accent for active items
// ═══════════════════════════════════════════════════════════════

@Composable
fun BottomNav(
    items: List<BottomNavItem>,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = SurfaceDark,
        contentColor = TextPrimary,
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        SurfaceDark
                    )
                )
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            // Animated colors
            val iconColor by animateColorAsState(
                targetValue = if (isSelected) Orange300 else TextTertiary,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "icon_color"
            )

            val textColor by animateColorAsState(
                targetValue = if (isSelected) Orange300 else TextTertiary,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "text_color"
            )

            NavigationBarItem(
                icon = {
                    Box(
                        modifier = Modifier
                            .then(
                                if (isSelected) {
                                    Modifier
                                        .shadow(
                                            elevation = 8.dp,
                                            shape = RoundedCornerShape(12.dp),
                                            ambientColor = Orange300.copy(alpha = 0.2f),
                                            spotColor = Orange300.copy(alpha = 0.2f)
                                        )
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Orange300.copy(alpha = 0.15f),
                                                    Orange300.copy(alpha = 0.05f)
                                                )
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 16.dp, vertical = 6.dp)
                                } else {
                                    Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = iconColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
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
