package com.openclaude.android.ui.screens.chat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclaude.android.core.ui.components.ChatBubble
import com.openclaude.android.core.ui.components.MessageInput
import com.openclaude.android.core.ui.components.ProviderChip
import com.openclaude.android.core.ui.theme.*
import com.openclaude.android.data.model.Model
import com.openclaude.android.data.model.Provider
import com.openclaude.android.data.remote.ApiError

// ═══════════════════════════════════════════════════════════════
// LINEAR-STYLE CHAT SCREEN
// Ultra-minimal dark mode, precision-engineered
// ═══════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String?,
    onNavigateToConversation: (String) -> Unit,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    val models = remember(uiState.currentProvider) {
        Model.defaultModels(uiState.currentProvider)
    }

    val isAtBottom by remember {
        derivedStateOf {
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            totalItems == 0 || lastVisibleIndex >= totalItems - 2
        }
    }

    val hasError = remember(uiState.error) { uiState.error != null }
    val hasStreamingMessage = remember(uiState.messages) {
        uiState.messages.any { it.isStreaming }
    }

    LaunchedEffect(conversationId) {
        viewModel.initialize(conversationId)
    }

    LaunchedEffect(uiState.messages.size, isAtBottom) {
        if (uiState.messages.isNotEmpty() && (isAtBottom || uiState.messages.size <= 1)) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CanvasBlack)
    ) {
        // ── Offline Banner ─────────────────────────────────────
        AnimatedVisibility(visible = !isOnline) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StatusRed.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CloudOff,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = StatusRed
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "No internet connection",
                        style = MaterialTheme.typography.bodySmall,
                        color = StatusRed,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // ── Chat Header ────────────────────────────────────────
        LinearChatHeader(
            currentProvider = uiState.currentProvider,
            currentModel = uiState.currentModel,
            models = models,
            onProviderSelected = { viewModel.setProvider(it) },
            onModelSelected = { viewModel.setModel(it) },
            onNewChat = { viewModel.newChat() }
        )

        // ── Error Banner ───────────────────────────────────────
        AnimatedVisibility(
            visible = hasError,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            uiState.error?.let { error ->
                LinearErrorBanner(
                    error = error,
                    canRetry = uiState.canRetry,
                    onDismiss = { viewModel.clearError() },
                    onRetry = { viewModel.retryLastMessage() }
                )
            }
        }

        // ── Messages ───────────────────────────────────────────
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("chat_list"),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            if (uiState.messages.isEmpty() && !uiState.isLoading) {
                item(key = "empty_state") {
                    LinearEmptyState(provider = uiState.currentProvider)
                }
            }

            items(
                items = uiState.messages,
                key = { it.id },
                contentType = { if (it.role == "user") "user_message" else "assistant_message" }
            ) { message ->
                ChatBubble(message = message)
            }

            if (uiState.isLoading && !hasStreamingMessage) {
                item(key = "loading_indicator") {
                    LinearThinkingIndicator()
                }
            }
        }

        // ── Input Area ─────────────────────────────────────────
        MessageInput(
            onSendMessage = { viewModel.sendMessage(it) },
            isLoading = uiState.isLoading
        )
    }
}

// ── Linear Chat Header ────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LinearChatHeader(
    currentProvider: Provider,
    currentModel: Model,
    models: List<Model>,
    onProviderSelected: (Provider) -> Unit,
    onModelSelected: (Model) -> Unit,
    onNewChat: () -> Unit,
) {
    Surface(
        color = CanvasBlack,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Provider + Model chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProviderChip(
                    selectedProvider = currentProvider,
                    onProviderSelected = onProviderSelected
                )

                var modelExpanded by remember { mutableStateOf(false) }

                // Model selector — minimal chip
                AssistChip(
                    onClick = { modelExpanded = true },
                    label = {
                        Text(
                            text = currentModel.name,
                            color = TextSecondary,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Memory,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = TextTertiary
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = SurfaceLevel3,
                        labelColor = TextSecondary,
                        leadingIconContentColor = TextTertiary
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        borderColor = BorderPrimary,
                        enabled = true
                    )
                )

                DropdownMenu(
                    expanded = modelExpanded,
                    onDismissRequest = { modelExpanded = false }
                ) {
                    models.forEach { model ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = model.name,
                                    color = if (model == currentModel) AccentViolet else TextPrimary
                                )
                            },
                            onClick = {
                                onModelSelected(model)
                                modelExpanded = false
                            }
                        )
                    }
                }
            }

            // Right: New chat button — subtle
            IconButton(
                onClick = onNewChat,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SurfaceLevel3)
                    .border(
                        width = 1.dp,
                        color = BorderPrimary,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "New Chat",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ── Linear Error Banner ───────────────────────────────────────
@Composable
private fun LinearErrorBanner(
    error: String,
    canRetry: Boolean,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(StatusRed.copy(alpha = 0.06f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Outlined.ErrorOutline,
                        contentDescription = null,
                        tint = StatusRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        color = StatusRed,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = StatusRed.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            if (canRetry) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StatusRed.copy(alpha = 0.1f),
                        contentColor = StatusRed
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Retry",
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

// ── Linear Empty State ────────────────────────────────────────
@Composable
private fun LinearEmptyState(provider: Provider) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Subtle icon
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(SurfaceLevel3)
                .border(
                    width = 1.dp,
                    color = BorderPrimary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = AccentViolet
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Start a conversation",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Chat with ${provider.displayName}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextTertiary,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Quick action chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickActionChip(
                icon = Icons.Outlined.Code,
                label = "Code"
            )
            QuickActionChip(
                icon = Icons.Outlined.Edit,
                label = "Write"
            )
            QuickActionChip(
                icon = Icons.Outlined.Lightbulb,
                label = "Ideas"
            )
        }
    }
}

// ── Quick Action Chip ─────────────────────────────────────────
@Composable
private fun QuickActionChip(
    icon: ImageVector,
    label: String,
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = SurfaceLevel3,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = BorderPrimary,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = TextTertiary
            )
            Text(
                text = label,
                fontSize = 13.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

// ── Linear Thinking Indicator ─────────────────────────────────
@Composable
private fun LinearThinkingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            color = AccentViolet,
            strokeWidth = 2.dp
        )
        Text(
            text = "Thinking...",
            color = TextTertiary,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Normal
        )
    }
}
