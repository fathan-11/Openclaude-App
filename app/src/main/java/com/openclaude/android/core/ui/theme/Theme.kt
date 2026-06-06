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
// LINEAR-INSPIRED DARK THEME
// Ultra-minimal, precision-engineered dark mode
// ═══════════════════════════════════════════════════════════════

private val LinearDarkScheme = darkColorScheme(
    // Primary — Brand Indigo
    primary = BrandIndigo,
    onPrimary = Color.White,
    primaryContainer = AccentViolet,
    onPrimaryContainer = TextPrimary,

    // Secondary — Accent Violet
    secondary = AccentViolet,
    onSecondary = Color.White,
    secondaryContainer = SurfaceLevel3,
    onSecondaryContainer = TextPrimary,

    // Tertiary — Status Green
    tertiary = StatusGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF10B981).copy(alpha = 0.1f),
    onTertiaryContainer = StatusGreen,

    // Background — Canvas Black
    background = CanvasBlack,
    onBackground = TextPrimary,

    // Surface — Panel Dark
    surface = PanelDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceLevel3,
    onSurfaceVariant = TextSecondary,

    // Error — Red
    error = StatusRed,
    onError = Color.White,
    errorContainer = Color(0xFFEF4444).copy(alpha = 0.1f),
    onErrorContainer = StatusRed,

    // Outline — Border System
    outline = BorderPrimary,
    outlineVariant = BorderSecondary,
)

// ── Light Theme (Fallback) ────────────────────────────────────
private val LinearLightScheme = lightColorScheme(
    primary = BrandIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8E9FB),
    onPrimaryContainer = BrandIndigo,
    secondary = AccentViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF0F0FF),
    onSecondaryContainer = AccentViolet,
    tertiary = StatusGreen,
    onTertiary = Color.White,
    background = Color(0xFFF7F8F8),
    onBackground = Color(0xFF09090B),
    surface = Color.White,
    onSurface = Color(0xFF09090B),
    surfaceVariant = Color(0xFFF3F4F5),
    onSurfaceVariant = Color(0xFF71717A),
    error = StatusRed,
    onError = Color.White,
    outline = Color(0xFFD0D6E0),
    outlineVariant = Color(0xFFF3F4F5),
)

@Composable
fun OpenClaudeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Always use dark theme for modern UI
    val colorScheme = LinearDarkScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Dark status bar
            window.statusBarColor = CanvasBlack.toArgb()
            window.navigationBarColor = PanelDark.toArgb()
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
