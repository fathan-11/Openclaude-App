package com.example.repopattern.ui.screens.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repopattern.data.model.User
import com.example.repopattern.data.repository.Resource
import com.example.repopattern.domain.usecase.GetUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserListUiState>(UserListUiState.Loading)
    val uiState: StateFlow<UserListUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            getUsersUseCase().collect { resource ->
                _uiState.value = when (resource) {
                    is Resource.Loading -> UserListUiState.Loading
                    is Resource.Success -> {
                        val users = resource.data ?: emptyList()
                        if (users.isEmpty()) UserListUiState.Empty
                        else UserListUiState.Success(users = users, isRefreshing = false)
                    }
                    is Resource.Error -> UserListUiState.Error(resource.message ?: "An unknown error occurred.")
                }
            }
        }
    }

    fun refresh() {
        val current = _uiState.value
        if (current is UserListUiState.Success) {
            _uiState.value = current.copy(isRefreshing = true)
        }
        loadUsers()
    }
}

sealed class UserListUiState {
    data object Loading : UserListUiState()
    data object Empty : UserListUiState()
    data class Success(val users: List<User>, val isRefreshing: Boolean = false) : UserListUiState()
    data class Error(val message: String) : UserListUiState()
}
