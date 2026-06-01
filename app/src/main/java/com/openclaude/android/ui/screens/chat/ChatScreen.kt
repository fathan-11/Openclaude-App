package com.openclaude.android.ui.screens.chat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclaude.android.core.ui.components.ChatBubble
import com.openclaude.android.core.ui.components.MessageInput
import com.openclaude.android.core.ui.components.ProviderChip
import com.openclaude.android.data.model.Model
import com.openclaude.android.data.model.Provider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String?,
    onNavigateToConversation: (String) -> Unit,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // Derive values with remember to avoid recomposition
    val models = remember(uiState.currentProvider) {
        Model.defaultModels(uiState.currentProvider)
    }

    // Track whether the list is at the bottom using derivedStateOf
    val isAtBottom by remember {
        derivedStateOf {
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            totalItems == 0 || lastVisibleIndex >= totalItems - 2
        }
    }

    // Derive whether there's an active error for the banner
    val hasError = remember(uiState.error) { uiState.error != null }
    val hasStreamingMessage = remember(uiState.messages) {
        uiState.messages.any { it.isStreaming }
    }

    LaunchedEffect(conversationId) {
        viewModel.initialize(conversationId)
    }

    // Auto-scroll only when user is at the bottom
    LaunchedEffect(uiState.messages.size, isAtBottom) {
        if (uiState.messages.isNotEmpty() && (isAtBottom || uiState.messages.size <= 1)) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Chat header with provider/model selector
        ChatHeader(
            currentProvider = uiState.currentProvider,
            currentModel = uiState.currentModel,
            models = models,
            onProviderSelected = { viewModel.setProvider(it) },
            onModelSelected = { viewModel.setModel(it) },
            onNewChat = { viewModel.newChat() }
        )

        // Error banner with retry button
        AnimatedVisibility(
            visible = hasError,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            uiState.error?.let { error ->
                ErrorBanner(
                    error = error,
                    canRetry = uiState.canRetry,
                    onDismiss = { viewModel.clearError() },
                    onRetry = { viewModel.retryLastMessage() }
                )
            }
        }

        // Messages
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
                    EmptyChatMessage(provider = uiState.currentProvider)
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
                    ThinkingIndicator()
                }
            }
        }

        // Input
        MessageInput(
            onSendMessage = { viewModel.sendMessage(it) },
            isLoading = uiState.isLoading
        )
    }
}

@Composable
private fun ChatHeader(
    currentProvider: Provider,
    currentModel: Model,
    models: List<Model>,
    onProviderSelected: (Provider) -> Unit,
    onModelSelected: (Model) -> Unit,
    onNewChat: () -> Unit,
) {
    Surface(
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProviderChip(
                    selectedProvider = currentProvider,
                    onProviderSelected = onProviderSelected
                )

                var modelExpanded by remember { mutableStateOf(false) }

                AssistChip(
                    onClick = { modelExpanded = true },
                    label = { Text(currentModel.name) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Memory,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )

                DropdownMenu(
                    expanded = modelExpanded,
                    onDismissRequest = { modelExpanded = false }
                ) {
                    models.forEach { model ->
                        DropdownMenuItem(
                            text = { Text(model.name) },
                            onClick = {
                                onModelSelected(model)
                                modelExpanded = false
                            }
                        )
                    }
                }
            }

            IconButton(onClick = onNewChat) {
                Icon(Icons.Default.Add, contentDescription = "New Chat")
            }
        }
    }
}

@Composable
private fun ErrorBanner(
    error: String,
    canRetry: Boolean,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            if (canRetry) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
private fun EmptyChatMessage(provider: Provider) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ChatBubbleOutline,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Start a conversation",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Send a message to begin chatting with ${provider.displayName}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ThinkingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Thinking...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Test tag for benchmark automation
private const val CHAT_LIST_TAG = "chat_list"
