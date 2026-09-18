package com.soilhelp.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soilhelp.android.data.ReadingResponse
import com.soilhelp.android.data.TokenManager
import com.soilhelp.android.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ReadingsUiState {
    object Loading : ReadingsUiState()
    data class Success(val latest: ReadingResponse?, val history: List<ReadingResponse>) : ReadingsUiState()
    data class Error(val message: String) : ReadingsUiState()
}

class ReadingsViewModel(tokenManager: TokenManager) : ViewModel() {

    private val api = RetrofitClient.getApiService(tokenManager)

    private val _uiState = MutableStateFlow<ReadingsUiState>(ReadingsUiState.Loading)
    val uiState: StateFlow<ReadingsUiState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = ReadingsUiState.Loading
            try {
                val latestResponse = api.getLatestReading()
                val historyResponse = api.getHistory(50)

                val latest = if (latestResponse.isSuccessful) latestResponse.body() else null
                val history = if (historyResponse.isSuccessful) historyResponse.body() ?: emptyList() else emptyList()

                _uiState.value = ReadingsUiState.Success(latest, history)
            } catch (e: Exception) {
                _uiState.value = ReadingsUiState.Error("Couldn't load readings: ${e.message}")
            }
        }
    }
}
