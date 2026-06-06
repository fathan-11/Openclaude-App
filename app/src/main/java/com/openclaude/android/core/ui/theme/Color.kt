package com.openclaude.android.core.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════
// OPENCLAUDE — LINEAR-INSPIRED DARK THEME
// Ultra-minimal dark mode with indigo-violet accent
// Based on Linear App design system
// ═══════════════════════════════════════════════════════════════

// ── Dark Foundation (Luminance Stack) ─────────────────────────
val CanvasBlack = Color(0xFF08090A)       // Deepest canvas
val PanelDark = Color(0xFF0F1011)         // Sidebar/panel backgrounds
val SurfaceLevel3 = Color(0xFF191A1B)     // Elevated surfaces, cards
val SurfaceLevel2 = Color(0xFF28282C)     // Hover states, lightest dark

// ── Brand Indigo (Primary Accent) ────────────────────────────
val BrandIndigo = Color(0xFF5E6AD2)       // Primary brand — CTAs, active states
val AccentViolet = Color(0xFF7170FF)      // Brighter interactive accent
val AccentHover = Color(0xFF828FFF)       // Hover state on accent
val BrandIndigoMuted = Color(0xFF7A7FAD)  // Muted indigo for subtle use

// ── Text Hierarchy ───────────────────────────────────────────
val TextPrimary = Color(0xFFF7F8F8)       // Near-white — main text
val TextSecondary = Color(0xFFD0D6E0)     // Cool silver — body, descriptions
val TextTertiary = Color(0xFF8A8F98)      // Muted gray — placeholders, metadata
val TextQuaternary = Color(0xFF62666D)    // Most subdued — timestamps, disabled

// ── Border System ────────────────────────────────────────────
val BorderPrimary = Color(0xFF23252A)     // Prominent separations
val BorderSecondary = Color(0xFF34343A)   // Slightly lighter
val BorderTertiary = Color(0xFF3E3E44)    // Lightest solid border

// ── Status Colors ────────────────────────────────────────────
val StatusGreen = Color(0xFF27A644)       // Success, active, online
val StatusEmerald = Color(0xFF10B981)     // Completion, pills
val StatusRed = Color(0xFFEF4444)         // Error, destructive
val StatusAmber = Color(0xFFF59E0B)       // Warning, loading

// ── Semantic Aliases ─────────────────────────────────────────
val DarkBackground = CanvasBlack
val DarkSurface = PanelDark
val DarkSurfaceVariant = SurfaceLevel3
val DarkOnSurface = TextPrimary
val DarkOnSurfaceVariant = TextSecondary

// ── Code Syntax Colors ───────────────────────────────────────
val CodeBackground = Color(0xFF0F1011)
val CodeForeground = Color(0xFFD0D6E0)
val CodeKeyword = Color(0xFF7170FF)       // Violet for keywords
val CodeString = Color(0xFF10B981)        // Emerald for strings
val CodeComment = Color(0xFF62666D)       // Muted for comments
val CodeFunction = Color(0xFF828FFF)      // Light violet for functions
val CodeNumber = Color(0xFFF59E0B)        // Amber for numbers

// ── Gradient Stops ───────────────────────────────────────────
val GradientStart = Color(0xFF5E6AD2)
val GradientMid = Color(0xFF7170FF)
val GradientEnd = Color(0xFF828FFF)

// ── Glass Morphism (Subtle) ──────────────────────────────────
val GlassBackground = Color(0x05FFFFFF)   // Ultra-subtle white
val GlassBorder = Color(0x08FFFFFF)       // Whisper-thin border
val GlassSurface = Color(0x02FFFFFF)      // Near-invisible surface

// ── Legacy Color Aliases ─────────────────────────────────────
val Indigo500 = Color(0xFF5E6AD2)
val Violet400 = Color(0xFF7170FF)

// ── Backward Compatibility (Orange → Indigo) ─────────────────
val Orange300 = BrandIndigo
val OrangeGlow = AccentViolet
val PurpleAccent = AccentViolet
val DeepBlack = CanvasBlack
val SurfaceDark = PanelDark
val SurfaceElevated = SurfaceLevel3
val BorderDark = BorderPrimary
val CreamLight = TextPrimary
val GreenSuccess = StatusGreen
val GreenLight = Color(0xFF10B981).copy(alpha = 0.1f)
val RedError = StatusRed
val RedLight = Color(0xFFEF4444).copy(alpha = 0.1f)
