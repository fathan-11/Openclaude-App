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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UserListUiState {
    object Loading : UserListUiState()
    data class Success(val users: List<User>, val isRefreshing: Boolean = false) : UserListUiState()
    data class Error(val message: String) : UserListUiState()
    object Empty : UserListUiState()
}

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserListUiState>(UserListUiState.Loading)
    val uiState: StateFlow<UserListUiState> = _uiState.asStateFlow()

    init { loadUsers() }

    fun loadUsers() {
        viewModelScope.launch {
            getUsersUseCase().collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        if (_uiState.value !is UserListUiState.Success) {
                            _uiState.value = UserListUiState.Loading
                        } else {
                            _uiState.update { if (it is UserListUiState.Success) it.copy(isRefreshing = true) else it }
                        }
                    }
                    is Resource.Success -> {
                        _uiState.value = if (resource.data.isEmpty()) UserListUiState.Empty
                        else UserListUiState.Success(resource.data, isRefreshing = false)
                    }
                    is Resource.Error -> { _uiState.value = UserListUiState.Error(resource.message) }
                }
            }
        }
    }

    fun refresh() { loadUsers() }
}
