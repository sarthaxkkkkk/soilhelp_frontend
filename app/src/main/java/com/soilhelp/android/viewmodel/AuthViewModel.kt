package com.soilhelp.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soilhelp.android.data.AuthRequest
import com.soilhelp.android.data.TokenManager
import com.soilhelp.android.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(private val tokenManager: TokenManager) : ViewModel() {

    private val api = RetrofitClient.getApiService(tokenManager)

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Username and password required")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val response = api.login(AuthRequest(username, password))
                if (response.isSuccessful && response.body() != null) {
                    tokenManager.saveToken(response.body()!!.token)
                    _uiState.value = AuthUiState.Success
                } else {
                    _uiState.value = AuthUiState.Error("Invalid username or password")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Network error: ${e.message}")
            }
        }
    }

    fun register(username: String, password: String) {
        if (username.isBlank() || password.length < 6) {
            _uiState.value = AuthUiState.Error("Password must be at least 6 characters")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val response = api.register(AuthRequest(username, password))
                if (response.isSuccessful && response.body() != null) {
                    tokenManager.saveToken(response.body()!!.token)
                    _uiState.value = AuthUiState.Success
                } else {
                    _uiState.value = AuthUiState.Error("Registration failed (username may be taken)")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Network error: ${e.message}")
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
