package com.openclaude.android.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openclaude.android.core.ui.theme.*
import kotlinx.coroutines.delay

// ═══════════════════════════════════════════════════════════════
// SPLASH SCREEN — Dark + Orange Accent
// Clean dark background with orange glow
// ═══════════════════════════════════════════════════════════════

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        alpha.animateTo(1f, animationSpec = tween(300))
        delay(1500)
        onTimeout()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                colors = listOf(
                    DeepBlack,
                    SurfaceDark
                )
            )
        ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Orange glowing circle
            Surface(
                modifier = Modifier.size(100.dp).scale(scale.value),
                shape = CircleShape,
                color = Orange300.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.SmartToy,
                        "OpenClaude",
                        modifier = Modifier.size(56.dp),
                        tint = Orange300
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Text(
                "OpenClaude",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.alpha(alpha.value)
            )
            Text(
                "AI Coding Agent",
                fontSize = 14.sp,
                color = TextSecondary,
                modifier = Modifier.alpha(alpha.value)
            )
        }
    }
}
