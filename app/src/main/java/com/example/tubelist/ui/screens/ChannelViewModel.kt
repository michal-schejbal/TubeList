package com.example.tubelist.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubelist.app.runCatchingCancellable
import com.example.tubelist.model.youtube.Channel
import com.example.tubelist.model.youtube.IYoutubeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface ChannelUiState {
    data object Loading : ChannelUiState
    data class Success(val channel: Channel) : ChannelUiState
    data class Error(val message: String?) : ChannelUiState
}

class ChannelViewModel(private val repository: IYoutubeRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<ChannelUiState>(ChannelUiState.Loading)
    val uiState: StateFlow<ChannelUiState> = _uiState

    fun getChannel(channelId: String) {
        if (isCached(channelId)) {
            return
        }

        viewModelScope.launch {
            _uiState.value = ChannelUiState.Loading
            runCatchingCancellable {
                repository.getChannel(channelId)
            }.onSuccess { detail ->
                if (detail != null) {
                    _uiState.value = ChannelUiState.Success(detail)
                } else {
                    _uiState.value = ChannelUiState.Error("Channel not found.")
                }
            }.onFailure { e ->
                _uiState.value = ChannelUiState.Error(e.message)
            }
        }
    }

    private fun isCached(channelId: String): Boolean {
        return (_uiState.value as? ChannelUiState.Success)?.channel?.channelId == channelId
    }
}