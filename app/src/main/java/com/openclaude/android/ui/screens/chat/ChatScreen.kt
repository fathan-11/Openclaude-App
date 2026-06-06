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
import androidx.compose.ui.draw.shadow
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
// MODERN BLACK CHAT SCREEN
// Pure black + glass morphism + electric accents
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
            .background(BlackPure)
    ) {
        // ── Offline Banner ─────────────────────────────────────
        AnimatedVisibility(visible = !isOnline) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Red500.copy(alpha = 0.15f),
                                Red500.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = Red500.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(0.dp)
                    )
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
                        modifier = Modifier.size(16.dp),
                        tint = Red400
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "No internet connection",
                        style = MaterialTheme.typography.bodySmall,
                        color = Red400,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // ── Chat Header ────────────────────────────────────────
        ModernChatHeader(
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
                ModernErrorBanner(
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
                    ModernEmptyState(provider = uiState.currentProvider)
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
                    ModernThinkingIndicator()
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

// ── Modern Chat Header ────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernChatHeader(
    currentProvider: Provider,
    currentModel: Model,
    models: List<Model>,
    onProviderSelected: (Provider) -> Unit,
    onModelSelected: (Model) -> Unit,
    onNewChat: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BlackSurface,
                        BlackPure
                    )
                )
            )
            .border(
                width = 0.5.dp,
                color = BlackBorder,
                shape = RoundedCornerShape(0.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
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

                // Model selector — glass morphism chip
                AssistChip(
                    onClick = { modelExpanded = true },
                    label = {
                        Text(
                            text = currentModel.name,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Memory,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Cyan400
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = BlackCard,
                        labelColor = TextPrimary,
                        leadingIconContentColor = Cyan400
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        borderColor = BlackBorder,
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
                                    color = if (model == currentModel) Violet500 else TextPrimary
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

            // Right: New chat button — glowing accent
            IconButton(
                onClick = onNewChat,
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp,
                        shape = CircleShape,
                        ambientColor = Violet500.copy(alpha = 0.2f),
                        spotColor = Violet500.copy(alpha = 0.2f)
                    )
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Violet500.copy(alpha = 0.15f),
                                Violet500.copy(alpha = 0.05f)
                            )
                        ),
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = Violet500.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "New Chat",
                    tint = Violet400,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ── Modern Error Banner ───────────────────────────────────────
@Composable
private fun ModernErrorBanner(
    error: String,
    canRetry: Boolean,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Red500.copy(alpha = 0.1f),
                        Red500.copy(alpha = 0.03f)
                    )
                )
            )
            .border(
                width = 0.5.dp,
                color = Red500.copy(alpha = 0.2f)
            )
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
                        tint = Red400,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        color = Red400,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = Red400.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (canRetry) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Red500.copy(alpha = 0.15f),
                        contentColor = Red400
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Red500.copy(alpha = 0.3f),
                                Red500.copy(alpha = 0.1f)
                            )
                        )
                    )
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Retry",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

// ── Modern Empty State ────────────────────────────────────────
@Composable
private fun ModernEmptyState(provider: Provider) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Glowing icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    ambientColor = Violet500.copy(alpha = 0.2f),
                    spotColor = Violet500.copy(alpha = 0.2f)
                )
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Violet500.copy(alpha = 0.15f),
                            Violet500.copy(alpha = 0.03f)
                        )
                    ),
                    shape = CircleShape
                )
                .border(
                    width = 1.dp,
                    color = Violet500.copy(alpha = 0.2f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = Violet400
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Start a conversation",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Send a message to chat with ${provider.displayName}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextTertiary,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

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

@Composable
private fun QuickActionChip(
    icon: ImageVector,
    label: String,
) {
    Surface(
        onClick = { /* TODO: quick action */ },
        shape = RoundedCornerShape(20.dp),
        color = BlackCard,
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    BlackBorder,
                    BlackBorder.copy(alpha = 0.5f)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Violet400
            )
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ── Modern Thinking Indicator ─────────────────────────────────
@Composable
private fun ModernThinkingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Animated dots
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Violet500.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = Violet400
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Thinking...",
            style = MaterialTheme.typography.bodyMedium,
            color = TextTertiary,
            fontWeight = FontWeight.Medium
        )
    }
}

// Test tag for benchmark automation
private const val CHAT_LIST_TAG = "chat_list"
