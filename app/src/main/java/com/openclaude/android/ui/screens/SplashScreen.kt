package com.openclaude.android.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openclaude.android.core.ui.theme.*
import kotlinx.coroutines.delay

// ═══════════════════════════════════════════════════════════════
// SPLASH SCREEN — Linear Dark Theme
// Ultra-minimal, clean dark with indigo accent
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
        modifier = Modifier
            .fillMaxSize()
            .background(CanvasBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // ── Icon Container ─────────────────────────────────
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .scale(scale.value)
                    .clip(CircleShape)
                    .background(SurfaceLevel3)
                    .border(1.dp, BorderPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.SmartToy,
                    "OpenClaude",
                    modifier = Modifier.size(40.dp),
                    tint = AccentViolet
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "OpenClaude",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                modifier = Modifier.alpha(alpha.value)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "AI Coding Agent",
                fontSize = 14.sp,
                color = TextTertiary,
                modifier = Modifier.alpha(alpha.value)
            )
        }
    }
}
