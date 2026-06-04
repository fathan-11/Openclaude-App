package com.openclaude.android.ui.screens.tools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ToolCall(
    val id: String,
    val name: String,
    val description: String,
    val parameters: Map<String, Any> = emptyMap(),
    val status: ToolStatus = ToolStatus.PENDING
)

enum class ToolStatus { PENDING, RUNNING, COMPLETED, FAILED }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolExecutionSheet(
    toolCall: ToolCall,
    onExecute: (ToolCall) -> Unit,
    onDismiss: () -> Unit,
    result: String? = null
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Build,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Tool: ${toolCall.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(
                toolCall.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            // Parameters
            if (toolCall.parameters.isNotEmpty()) {
                Text("Parameters", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        toolCall.parameters.forEach { (key, value) ->
                            Text(
                                "$key: $value",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Result
            if (result != null) {
                Spacer(Modifier.height(16.dp))
                Text("Result", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (toolCall.status == ToolStatus.FAILED)
                            MaterialTheme.colorScheme.errorContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        result,
                        modifier = Modifier.padding(12.dp),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = if (toolCall.status == ToolStatus.FAILED)
                            MaterialTheme.colorScheme.onErrorContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
                Spacer(Modifier.width(8.dp))
                when (toolCall.status) {
                    ToolStatus.PENDING -> {
                        Button(onClick = { onExecute(toolCall) }) {
                            Icon(Icons.Default.PlayArrow, null)
                            Spacer(Modifier.width(4.dp))
                            Text("Execute")
                        }
                    }
                    ToolStatus.RUNNING -> {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                    ToolStatus.COMPLETED -> {
                        Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
                            Icon(Icons.Default.Check, null)
                            Spacer(Modifier.width(4.dp))
                            Text("Done")
                        }
                    }
                    ToolStatus.FAILED -> {
                        Button(onClick = { onExecute(toolCall) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                            Icon(Icons.Default.Refresh, null)
                            Spacer(Modifier.width(4.dp))
                            Text("Retry")
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
