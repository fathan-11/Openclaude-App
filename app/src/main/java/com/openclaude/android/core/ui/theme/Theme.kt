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
// DARK MODE + ORANGE ACCENT THEME (v3)
// MeetGen-inspired: Dark charcoal + warm orange + cream cards
// ═══════════════════════════════════════════════════════════════

private val DarkOrangeScheme = darkColorScheme(
    // Primary — Orange Accent
    primary = Orange300,
    onPrimary = TextOnOrange,
    primaryContainer = Orange400,
    onPrimaryContainer = CreamLight,

    // Secondary — Purple Accent
    secondary = PurpleAccent,
    onSecondary = DeepBlack,
    secondaryContainer = SurfaceElevated,
    onSecondaryContainer = TextPrimary,

    // Tertiary — Green Success
    tertiary = GreenSuccess,
    onTertiary = DeepBlack,
    tertiaryContainer = GreenLight,
    onTertiaryContainer = GreenSuccess,

    // Background — Deep Black
    background = DeepBlack,
    onBackground = TextPrimary,

    // Surface — Dark Surface
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,

    // Error — Orange Red
    error = RedError,
    onError = DeepBlack,
    errorContainer = RedLight,
    onErrorContainer = RedError,

    // Outline
    outline = BorderDark,
    outlineVariant = SurfaceElevated,
)

// ── Light Theme (Fallback) ────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary = Orange300,
    onPrimary = Color.White,
    primaryContainer = Orange100,
    onPrimaryContainer = Orange500,
    secondary = PurpleAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF5F0FF),
    onSecondaryContainer = PurpleAccent,
    tertiary = GreenSuccess,
    onTertiary = Color.White,
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF09090B),
    surface = Color.White,
    onSurface = Color(0xFF09090B),
    surfaceVariant = Color(0xFFF4F4F5),
    onSurfaceVariant = Color(0xFF71717A),
    error = RedError,
    onError = Color.White,
    outline = Color(0xFFE4E4E7),
    outlineVariant = Color(0xFFF4F4F5),
)

@Composable
fun OpenClaudeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Always use dark theme for modern UI
    val colorScheme = DarkOrangeScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Dark status bar
            window.statusBarColor = DeepBlack.toArgb()
            window.navigationBarColor = SurfaceDark.toArgb()
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
