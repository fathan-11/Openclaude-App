package com.openclaude.android.core.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.delay

@Composable
fun StreamingText(
    text: String,
    color: Color,
    style: TextStyle,
    modifier: Modifier = Modifier,
    charDelayMs: Long = 15L
) {
    var displayedText by remember { mutableStateOf("") }
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(text) {
        displayedText = ""
        currentIndex = 0
        while (currentIndex < text.length) {
            currentIndex++
            displayedText = text.substring(0, currentIndex)
            delay(charDelayMs)
        }
    }

    Text(
        text = displayedText,
        color = color,
        style = style,
        modifier = modifier
    )
}
