package com.openclaude.android.core.util

import androidx.compose.ui.graphics.Color

object AnsiColorParser {

    // Standard 16 colors
    private val standardColors = mapOf(
        0 to Color(0xFF000000),   // Black
        1 to Color(0xFFCC0000),   // Red
        2 to Color(0xFF4E9A06),   // Green
        3 to Color(0xC4A000),     // Yellow
        4 to Color(0xFF3465A4),   // Blue
        5 to Color(0xFF75507B),   // Magenta
        6 to Color(0xFF06989A),   // Cyan
        7 to Color(0xFFD3D7CF),   // White
        8 to Color(0xFF555753),   // Bright Black
        9 to Color(0xFFEF2929),   // Bright Red
        10 to Color(0xFF8AE234),  // Bright Green
        11 to Color(0xFFFCE94F),  // Bright Yellow
        12 to Color(0xFF729FCF),  // Bright Blue
        13 to Color(0xFFAD7FA8),  // Bright Magenta
        14 to Color(0xFF34E2E2),  // Bright Cyan
        15 to Color(0xFFEEEEEC)   // Bright White
    )

    // ANSI 256 color palette
    private val ansi256Colors: List<Color> by lazy {
        val colors = mutableListOf<Color>()

        // 0-7: Standard colors
        colors.addAll(standardColors.values)

        // 8-15: Bright colors
        colors.addAll(listOf(
            Color(0xFF555753), Color(0xFFEF2929), Color(0xFF8AE234), Color(0xFFFCE94F),
            Color(0xFF729FCF), Color(0xFFAD7FA8), Color(0xFF34E2E2), Color(0xFFEEEEEC)
        ))

        // 16-231: 6x6x6 color cube
        val rgbValues = intArrayOf(0, 95, 135, 175, 215, 255)
        for (r in rgbValues) {
            for (g in rgbValues) {
                for (b in rgbValues) {
                    colors.add(Color(r, g, b))
                }
            }
        }

        // 232-255: Grayscale ramp
        for (i in 0..23) {
            val gray = 8 + i * 10
            colors.add(Color(gray, gray, gray))
        }

        colors
    }

    data class AnsiStyle(
        val color: Color? = null,
        val backgroundColor: Color? = null,
        val isBold: Boolean = false,
        val isItalic: Boolean = false,
        val isUnderline: Boolean = false,
        val isStrikethrough: Boolean = false
    )

    fun parseAnsiCode(code: Int): AnsiStyle {
        return when {
            code == 0 -> AnsiStyle() // Reset
            code == 1 -> AnsiStyle(isBold = true)
            code == 3 -> AnsiStyle(isItalic = true)
            code == 4 -> AnsiStyle(isUnderline = true)
            code == 9 -> AnsiStyle(isStrikethrough = true)
            code in 30..37 -> AnsiStyle(color = standardColors[code - 30])
            code in 40..47 -> AnsiStyle(backgroundColor = standardColors[code - 40])
            code in 90..97 -> AnsiStyle(color = standardColors[code - 90 + 8])
            code in 100..107 -> AnsiStyle(backgroundColor = standardColors[code - 100 + 8])
            else -> AnsiStyle()
        }
    }

    fun parse256Color(code: Int): Color? {
        return if (code in ansi256Colors.indices) ansi256Colors[code] else null
    }

    fun getAnsi256Color(code: Int): Color? {
        return when {
            code in 0..7 -> standardColors[code]
            code in 8..15 -> ansi256Colors[code]
            code in 16..231 -> ansi256Colors[code]
            code in 232..255 -> ansi256Colors[code]
            else -> null
        }
    }
}
