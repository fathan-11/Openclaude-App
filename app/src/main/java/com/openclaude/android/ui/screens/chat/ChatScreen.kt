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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclaude.android.core.ui.components.ChatBubble
import com.openclaude.android.core.ui.components.MessageInput
import com.openclaude.android.core.ui.components.ProviderChip
import com.openclaude.android.data.model.Model
import com.openclaude.android.data.model.Provider
import com.openclaude.android.data.remote.ApiError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String?,
    onNavigateToConversation: (String) -> Unit,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(conversationId) {
        viewModel.initialize(conversationId)
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Chat header with provider/model selector
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
                        selectedProvider = uiState.currentProvider,
                        onProviderSelected = { viewModel.setProvider(it) }
                    )

                    var modelExpanded by remember { mutableStateOf(false) }
                    val models = Model.defaultModels(uiState.currentProvider)

                    AssistChip(
                        onClick = { modelExpanded = true },
                        label = { Text(uiState.currentModel.name) },
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
                                    viewModel.setModel(model)
                                    modelExpanded = false
                                }
                            )
                        }
                    }
                }

                IconButton(onClick = { viewModel.newChat() }) {
                    Icon(Icons.Default.Add, contentDescription = "New Chat")
                }
            }
        }

        // Error banner with retry button and error type icon
        AnimatedVisibility(
            visible = uiState.error != null,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            uiState.error?.let { error ->
                ErrorBanner(
                    error = error,
                    errorType = uiState.errorType,
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
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            if (uiState.messages.isEmpty() && !uiState.isLoading) {
                item {
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
                            text = "Send a message to begin chatting with ${uiState.currentProvider.displayName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            items(uiState.messages, key = { it.id }) { message ->
                ChatBubble(message = message)
            }

            if (uiState.isLoading && uiState.messages.none { it.isStreaming }) {
                item {
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
private fun ErrorBanner(
    error: String,
    errorType: ApiError?,
    canRetry: Boolean,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
) {
    val icon = when (errorType) {
        is ApiError.NetworkError -> Icons.Default.CloudOff
        is ApiError.TimeoutError -> Icons.Default.Timer
        is ApiError.AuthError -> Icons.Default.Lock
        is ApiError.RateLimitError -> Icons.Default.Speed
        is ApiError.ServerError -> Icons.Default.ErrorOutline
        else -> Icons.Default.Warning
    }

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
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
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
            
            // Show retry button for transient errors
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
