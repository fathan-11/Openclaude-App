package com.openclaude.android.core.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════
// OPENCLAUDE ANDROID — DARK MODE + ORANGE ACCENT (v3)
// Design: MeetGen-inspired dark theme with warm orange accent
// ═══════════════════════════════════════════════════════════════

// ── Dark Foundation ──────────────────────────────────────────
val DeepBlack = Color(0xFF0F0F0F)      // Deepest background
val SurfaceDark = Color(0xFF1A1A1A)    // Main surface
val SurfaceElevated = Color(0xFF2A2A2A) // Elevated cards/surfaces
val BorderDark = Color(0xFF333333)     // Borders and dividers

// ── Orange Accent (Primary) ─────────────────────────────────
val Orange100 = Color(0xFFFFB088)      // Soft orange
val Orange200 = Color(0xFFF4845F)      // Light orange
val Orange300 = Color(0xFFE86F48)      // Main accent
val Orange400 = Color(0xFFD45A35)      // Darker orange
val Orange500 = Color(0xFFBF4522)      // Darkest orange
val OrangeGlow = Color(0xFFE86F48)     // For glow effects

// ── Cream / Light Cards ─────────────────────────────────────
val CreamLight = Color(0xFFF5F0E6)     // Main cream card
val CreamWhite = Color(0xFFFFF8F0)     // Lighter cream

// ── Signal Colors ────────────────────────────────────────────
val GreenSuccess = Color(0xFF3DAF68)   // Success/online
val GreenLight = Color(0xFFD4F5E4)     // Success background
val RedError = Color(0xFFE86F48)       // Error (uses orange)
val RedLight = Color(0xFFFFE8DD)       // Error background
val PurpleAccent = Color(0xFFB49AFF)   // Secondary accent

// ── Text Hierarchy ───────────────────────────────────────────
val TextPrimary = Color(0xFFF5F0E6)    // Main text - cream white
val TextSecondary = Color(0xFF888888)  // Muted text
val TextTertiary = Color(0xFF555555)   // Disabled / hint
val TextOnOrange = Color(0xFF1A1A1A)   // Text on orange accent
val TextOnCream = Color(0xFF1A1A1A)    // Text on cream cards

// ── Semantic Colors ──────────────────────────────────────────
val DarkBackground = DeepBlack
val DarkSurface = SurfaceDark
val DarkSurfaceVariant = SurfaceElevated
val DarkOnSurface = TextPrimary
val DarkOnSurfaceVariant = TextSecondary

// ── Code Syntax Colors ───────────────────────────────────────
val CodeBackground = Color(0xFF0F0F0F)
val CodeForeground = Color(0xFFF5F0E6)
val CodeKeyword = Color(0xFFB49AFF)    // Purple for keywords
val CodeString = Color(0xFFFFB088)     // Orange for strings
val CodeComment = Color(0xFF555555)    // Muted for comments
val CodeFunction = Color(0xFFE86F48)   // Orange for functions

// ── Gradient Stops ───────────────────────────────────────────
val GradientStart = Color(0xFFE86F48)
val GradientMid = Color(0xFFF4845F)
val GradientEnd = Color(0xFFFFB088)

// ── Glass Morphism ───────────────────────────────────────────
val GlassBackground = Color(0x15FFFFFF)
val GlassBorder = Color(0x25FFFFFF)
val GlassSurface = Color(0x0DFFFFFF)
