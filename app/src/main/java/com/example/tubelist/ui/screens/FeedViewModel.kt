package com.example.tubelist.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tubelist.model.youtube.IYoutubeRepository
import com.example.tubelist.model.youtube.SubscriptionItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class SortOption {
    RELEVANCE, ALPHABETICAL
}

data class FeedUiState(
    val isLoading: Boolean = false,
    val subscriptions: List<SubscriptionItem> = emptyList(),
    val error: String? = null,
    val sortOption: SortOption = SortOption.RELEVANCE,
    val searchQuery: String = ""
)

class FeedViewModel(private val repository: IYoutubeRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState

    private var originalList: List<SubscriptionItem> = emptyList()

    init {
        getSubscriptions()
    }

    fun getSubscriptions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                originalList = repository.getSubscriptions()
                filter()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun setSortOption(option: SortOption) {
        _uiState.value = _uiState.value.copy(sortOption = option)
        filter()
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        filter()
    }

    private fun filter() {
        val currentQuery = _uiState.value.searchQuery
        val currentSort = _uiState.value.sortOption

        val filtered = originalList.filter {
            it.title.contains(currentQuery, ignoreCase = true)
        }

        val sorted = when (currentSort) {
            SortOption.RELEVANCE -> filtered
            SortOption.ALPHABETICAL -> filtered.sortedBy { it.title.lowercase() }
        }

        _uiState.value = _uiState.value.copy(
            subscriptions = sorted,
            isLoading = false
        )
    }
}