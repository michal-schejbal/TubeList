package com.example.tubelist.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubelist.app.runCatchingCancellable
import com.example.tubelist.model.youtube.IYoutubeRepository
import com.example.tubelist.model.youtube.SubscriptionItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

enum class SortOption {
    RELEVANCE, ALPHABETICAL
}

sealed interface FeedUiState {
    data object Loading : FeedUiState
    data class Success(
        val subscriptions: List<SubscriptionItem> = emptyList(),
        val sortOption: SortOption = SortOption.RELEVANCE,
        val searchQuery: String = ""
    ) : FeedUiState
    data class Error(val message: String?) : FeedUiState
}

class FeedViewModel(private val repository: IYoutubeRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState

    private var originalList: List<SubscriptionItem> = emptyList()

    init {
        getSubscriptions()
    }

    fun getSubscriptions() {
        viewModelScope.launch {
            _uiState.value = FeedUiState.Loading
            runCatchingCancellable {
                repository.getSubscriptions()
            }.onSuccess { list ->
                originalList = list
                _uiState.value = FeedUiState.Success(subscriptions = originalList)
            }.onFailure { e ->
                _uiState.value = FeedUiState.Error(e.message)
            }
        }
    }

    fun setSortOption(option: SortOption) {
        val currentState = _uiState.value
        if (currentState is FeedUiState.Success) {
            _uiState.value = currentState.copy(sortOption = option)
            filter()
        }
    }

    fun setSearchQuery(query: String) {
        val currentState = _uiState.value
        if (currentState is FeedUiState.Success) {
            _uiState.value = currentState.copy(searchQuery = query)
            filter()
        }
    }

    private fun filter() {
        val currentState = _uiState.value
        if (currentState is FeedUiState.Success) {
            val filtered = originalList.filter {
                it.title.contains(currentState.searchQuery, ignoreCase = true)
            }

            val sorted = when (currentState.sortOption) {
                SortOption.RELEVANCE -> filtered
                SortOption.ALPHABETICAL -> filtered.sortedBy { it.title.lowercase() }
            }

            _uiState.value = currentState.copy(subscriptions = sorted)
        }
    }
}