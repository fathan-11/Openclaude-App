package com.example.repopattern.ui.screens.userlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.repopattern.ui.components.EmptyScreen
import com.example.repopattern.ui.components.ErrorScreen
import com.example.repopattern.ui.components.LoadingScreen
import com.example.repopattern.ui.components.UserCard

/**
 * UserListScreen — main screen showing all users.
 * Layout: Scaffold + CenterAlignedTopAppBar + LazyColumn
 * States: Loading (shimmer), Empty, Success, Error
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    onUserClick: (Int) -> Unit,
    viewModel: UserListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Users",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh users",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is UserListUiState.Loading -> {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    LoadingScreen(modifier = Modifier.padding(paddingValues))
                }
            }

            is UserListUiState.Empty -> {
                EmptyScreen(
                    message = "No users found.",
                    onRetry = { viewModel.loadUsers() },
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is UserListUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .semantics {
                            contentDescription = "User list with ${state.users.size} users"
                        },
                    contentPadding = PaddingValues(16.dp),  // spacing.lg
                    verticalArrangement = Arrangement.spacedBy(12.dp)  // spacing.md
                ) {
                    items(
                        items = state.users,
                        key = { it.id }
                    ) { user ->
                        UserCard(
                            user = user,
                            onClick = { onUserClick(user.id) }
                        )
                    }

                    // Footer: refresh button + count
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (state.isRefreshing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.padding(8.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                TextButton(onClick = { viewModel.refresh() }) {
                                    Text("Refresh")
                                }
                            }
                            Text(
                                text = "Showing ${state.users.size} users",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            is UserListUiState.Error -> {
                ErrorScreen(
                    message = state.message,
                    onRetry = { viewModel.loadUsers() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}
