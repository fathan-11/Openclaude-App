package com.openclaude.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openclaude.android.core.ui.theme.*

// ═══════════════════════════════════════════════════════════════
// MODERN BLACK TOP BAR
// Minimal + glass morphism + accent glow
// ═══════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Accent dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Violet500,
                                    Violet500.copy(alpha = 0.0f)
                                )
                            ),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { /* TODO: drawer */ }) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        actions = {
            IconButton(onClick = { /* TODO: notifications */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = TextPrimary,
            navigationIconContentColor = TextSecondary,
            actionIconContentColor = TextSecondary
        )
    )
}
