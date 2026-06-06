package com.openclaude.android.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openclaude.android.domain.model.MessageUiModel
import com.openclaude.android.core.ui.theme.*

// ═══════════════════════════════════════════════════════════════
// LINEAR-STYLE CHAT BUBBLE
// Minimal, clean, precision-engineered
// ═══════════════════════════════════════════════════════════════

@Composable
fun ChatBubble(
    message: MessageUiModel,
    modifier: Modifier = Modifier
) {
    val isUser = message.role == "user"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // ── Message Content ───────────────────────────────────
        if (isUser) {
            // User message — subtle brand container
            Box(
                modifier = Modifier
                    .widthIn(max = 320.dp)
                    .clip(RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp))
                    .background(BrandIndigo.copy(alpha = 0.15f))
                    .padding(12.dp)
            ) {
                if (message.isStreaming) {
                    StreamingText(
                        text = message.content,
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                } else {
                    MarkdownText(
                        markdown = message.content,
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        } else {
            // Assistant message — no background, clean text
            Box(
                modifier = Modifier
                    .widthIn(max = 380.dp)
                    .padding(horizontal = 4.dp)
            ) {
                if (message.isStreaming) {
                    StreamingText(
                        text = message.content,
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                } else {
                    MarkdownText(
                        markdown = message.content,
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }

        // ── Timestamp ─────────────────────────────────────────
        Text(
            text = formatTimestamp(message.timestamp),
            style = MaterialTheme.typography.labelSmall,
            color = TextQuaternary,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}
