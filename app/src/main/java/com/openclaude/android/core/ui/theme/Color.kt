package com.openclaude.android.core.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════
// OPENCLAUDE ANDROID — MODERN BLACK THEME
// Design: Pure black + electric accent + glass morphism
// ═══════════════════════════════════════════════════════════════

// ── Pure Black Foundation ──────────────────────────────────────
val BlackPure = Color(0xFF000000)
val BlackDeep = Color(0xFF0A0A0F)
val BlackRich = Color(0xFF0D0D12)
val BlackSurface = Color(0xFF111118)
val BlackCard = Color(0xFF16161F)
val BlackElevated = Color(0xFF1C1C28)
val BlackBorder = Color(0xFF252535)

// ── Electric Violet Accent (Primary) ──────────────────────────
val Violet100 = Color(0xFFF0E6FF)
val Violet200 = Color(0xFFD4B5FF)
val Violet300 = Color(0xFFB68AFF)
val Violet400 = Color(0xFF9966FF)
val Violet500 = Color(0xFF7C3AED)  // Main accent
val Violet600 = Color(0xFF6D28D9)
val Violet700 = Color(0xFF5B21B6)
val VioletGlow = Color(0xFF7C3AED)  // For glow effects

// ── Cyan Accent (Secondary) ──────────────────────────────────
val Cyan100 = Color(0xFFCFFAFE)
val Cyan200 = Color(0xFFA5F3FC)
val Cyan300 = Color(0xFF67E8F9)
val Cyan400 = Color(0xFF22D3EE)
val Cyan500 = Color(0xFF06B6D4)
val Cyan600 = Color(0xFF0891B2)
val CyanGlow = Color(0xFF22D3EE)

// ── Neon Green (Success / Online) ─────────────────────────────
val Green100 = Color(0xFFDCFCE7)
val Green300 = Color(0xFF86EFAC)
val Green400 = Color(0xFF4ADE80)
val Green500 = Color(0xFF22C55E)
val GreenGlow = Color(0xFF4ADE80)

// ── Signal Colors ─────────────────────────────────────────────
val Red400 = Color(0xFFF87171)
val Red500 = Color(0xFFEF4444)
val RedGlow = Color(0xFFF87171)
val Amber400 = Color(0xFFFBBF24)
val Amber500 = Color(0xFFF59E0B)

// ── Text Hierarchy ────────────────────────────────────────────
val TextPrimary = Color(0xFFF5F5F7)      // Main text - near white
val TextSecondary = Color(0xFFA1A1AA)    // Muted text
val TextTertiary = Color(0xFF71717A)     // Disabled / hint
val TextAccent = Color(0xFF9966FF)       // Links / highlights

// ── Semantic Colors ───────────────────────────────────────────
val DarkBackground = BlackPure
val DarkSurface = BlackSurface
val DarkSurfaceVariant = BlackCard
val DarkOnSurface = TextPrimary
val DarkOnSurfaceVariant = TextSecondary

val CodeBackground = Color(0xFF0D1117)   // GitHub dark
val CodeForeground = Color(0xFFE6EDF3)
val CodeKeyword = Color(0xFFFF7B72)
val CodeString = Color(0xFFA5D6FF)
val CodeComment = Color(0xFF8B949E)
val CodeFunction = Color(0xFFD2A8FF)

// ── Gradient Stops ────────────────────────────────────────────
val GradientStart = Color(0xFF7C3AED)
val GradientMid = Color(0xFF6D28D9)
val GradientEnd = Color(0xFF06B6D4)

// ── Glass Morphism ────────────────────────────────────────────
val GlassBackground = Color(0x15FFFFFF)  // 8% white
val GlassBorder = Color(0x25FFFFFF)      // 15% white
val GlassSurface = Color(0x0DFFFFFF)     // 5% white
