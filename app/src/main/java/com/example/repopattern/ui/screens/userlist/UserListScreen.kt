package com.example.repopattern.ui.screens.userlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.repopattern.ui.components.EmptyScreen
import com.example.repopattern.ui.components.ErrorScreen
import com.example.repopattern.ui.components.LoadingScreen
import com.example.repopattern.ui.components.UserCard

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
                title = { Text("Users") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is UserListUiState.Loading -> LoadingScreen(modifier = Modifier.padding(paddingValues))
            is UserListUiState.Success -> {
                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { viewModel.refresh() },
                    modifier = Modifier.padding(paddingValues)
                ) {
                    if (state.users.isEmpty()) {
                        EmptyScreen(message = "No users found.", onRetry = { viewModel.loadUsers() })
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.users, key = { it.id }) { user ->
                                UserCard(user = user, onClick = { onUserClick(user.id) })
                            }
                            item {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    TextButton(onClick = { viewModel.loadUsers() }) {
                                        Text("Refresh")
                                    }
                                    Text(
                                        text = "Showing ${'$'}{state.users.size} users",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
            is UserListUiState.Error -> ErrorScreen(
                state.message,
                onRetry = { viewModel.loadUsers() },
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}
