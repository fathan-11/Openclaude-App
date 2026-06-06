package com.openclaude.android.core.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ═══════════════════════════════════════════════════════════════
// MODERN BLACK THEME — Dark Mode Only (Primary)
// Pure black + electric violet + glass morphism
// ═══════════════════════════════════════════════════════════════

private val ModernBlackScheme = darkColorScheme(
    // Primary — Electric Violet
    primary = Violet500,
    onPrimary = Color.White,
    primaryContainer = Violet700,
    onPrimaryContainer = Violet100,

    // Secondary — Cyan
    secondary = Cyan400,
    onSecondary = BlackPure,
    secondaryContainer = Cyan600,
    onSecondaryContainer = Cyan100,

    // Tertiary — Green
    tertiary = Green400,
    onTertiary = BlackPure,
    tertiaryContainer = Green500,
    onTertiaryContainer = Green100,

    // Background — Pure Black
    background = BlackPure,
    onBackground = TextPrimary,

    // Surface — Near Black
    surface = BlackSurface,
    onSurface = TextPrimary,
    surfaceVariant = BlackCard,
    onSurfaceVariant = TextSecondary,

    // Error
    error = Red400,
    onError = BlackPure,
    errorContainer = Color(0xFF3D1515),
    onErrorContainer = Red400,

    // Outline
    outline = BlackBorder,
    outlineVariant = Color(0xFF1E1E2E),
)

// ── Light Theme (Fallback) ────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary = Violet600,
    onPrimary = Color.White,
    primaryContainer = Violet100,
    onPrimaryContainer = Violet700,
    secondary = Cyan500,
    onSecondary = Color.White,
    secondaryContainer = Cyan100,
    onSecondaryContainer = Cyan600,
    tertiary = Green500,
    onTertiary = Color.White,
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF09090B),
    surface = Color.White,
    onSurface = Color(0xFF09090B),
    surfaceVariant = Color(0xFFF4F4F5),
    onSurfaceVariant = Color(0xFF71717A),
    error = Red500,
    onError = Color.White,
    outline = Color(0xFFE4E4E7),
    outlineVariant = Color(0xFFF4F4F5),
)

@Composable
fun OpenClaudeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Always use dark theme for modern black UI
    val colorScheme = ModernBlackScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Pure black status bar
            window.statusBarColor = BlackPure.toArgb()
            window.navigationBarColor = BlackPure.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
