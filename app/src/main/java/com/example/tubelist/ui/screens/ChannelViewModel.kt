package com.example.tubelist.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubelist.model.youtube.Channel
import com.example.tubelist.model.youtube.IYoutubeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ChannelUiState(
    val isLoading: Boolean = false,
    val channel: Channel? = null,
    val error: String? = null
)

class ChannelViewModel(private val repository: IYoutubeRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ChannelUiState())
    val uiState: StateFlow<ChannelUiState> = _uiState

    fun getChannel(channelId: String) {
        if (isCached(channelId)) {
            return
        }

        viewModelScope.launch {
            _uiState.value = ChannelUiState(isLoading = true)
            try {
                val detail = repository.getChannel(channelId)
                _uiState.value = ChannelUiState(channel = detail)
            } catch (e: Exception) {
                _uiState.value = ChannelUiState(error = e.message)
            }
        }
    }

    private fun isCached(channelId: String): Boolean {
        return _uiState.value.channel?.channelId == channelId
    }
}