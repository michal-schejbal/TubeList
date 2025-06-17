package com.example.tubelist.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tubelist.model.youtube.SubscriptionItem
import com.example.tubelist.ui.components.LoadingComponent
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = koinViewModel(),
    onChannelClick: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    FeedScreenContent(
        modifier = modifier,
        uiState = state,
        onChannelClick = onChannelClick,
        onSortChange = { viewModel.setSortOption(it) },
        onSearchChange = { viewModel.setSearchQuery(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreenContent(
    modifier: Modifier = Modifier,
    uiState: FeedUiState,
    onChannelClick: (String) -> Unit = {},
    onSortChange: (SortOption) -> Unit = {},
    onSearchChange: (String) -> Unit = {}
) {
    val searchSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isSearchSheetVisible by remember { mutableStateOf(false) }

    val sortSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isSortSheetVisible by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column {
            FilterComponent(
                searchQuery = uiState.searchQuery,
                sortOption = uiState.sortOption,
                onSearchClick = {
                    isSearchSheetVisible = true
                },
                onSortClick = {
                    isSortSheetVisible = true
                }
            )

            when {
                uiState.isLoading -> LoadingComponent()
                uiState.subscriptions.isEmpty() -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No subscriptions found.")
                    }
                else -> LazyColumn {
                    items(uiState.subscriptions) { item ->
                        SubscriptionItem(subscription = item) {
                            onChannelClick(item.channelId)
                        }
                    }
                }
            }
        }

        if (isSearchSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = { isSearchSheetVisible = false },
                sheetState = searchSheetState,
                content = {
                    SearchBottomSheet(
                        currentQuery = uiState.searchQuery,
                        onSearch = {
                            onSearchChange(it)
                        },
                        onClose = { isSearchSheetVisible = false }
                    )
                }
            )
        }

        if (isSortSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = { isSortSheetVisible = false },
                sheetState = sortSheetState,
                content = {
                    SortBottomSheet(
                        selected = uiState.sortOption,
                        onSelect = {
                            onSortChange(it)
                        },
                        onClose = { isSortSheetVisible = false }
                    )
                }
            )
        }
    }
}

@Composable
internal fun SubscriptionItem(subscription: SubscriptionItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation()
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            if (subscription.thumbnails != null) {
                AsyncImage(
                    model = subscription.thumbnails.default?.url,
                    contentDescription = subscription.title,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(5.dp))
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Subscriptions,
                    contentDescription = subscription.title,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    tint = Color.DarkGray
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = subscription.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun FilterComponent(
    searchQuery: String,
    sortOption: SortOption,
    onSearchClick: () -> Unit,
    onSortClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(
            onClick = onSearchClick,
            label = { Text("Search: ${searchQuery.ifBlank { "none" }}") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )

        AssistChip(
            onClick = onSortClick,
            label = { Text("Sort: ${sortOption.name.lowercase().replaceFirstChar { it.uppercase() }}") },
            leadingIcon = { Icon(Icons.Default.Tune, contentDescription = null) }
        )
    }
}

@Composable
fun SearchBottomSheet(
    currentQuery: String,
    onSearch: (String) -> Unit,
    onClose: () -> Unit
) {
    var text by remember { mutableStateOf(currentQuery) }
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Search Subscriptions", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Search...") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                onSearch(text)
                keyboardController?.hide()
                coroutineScope.launch {
                    onClose()
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Apply")
        }
    }
}

@Composable
fun SortBottomSheet(
    selected: SortOption,
    onSelect: (SortOption) -> Unit,
    onClose: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Sort By", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        SortOption.entries.forEach { option ->
            ListItem(
                headlineContent = {
                    Text(option.name.lowercase().replaceFirstChar { it.uppercase() })
                },
                leadingContent = {
                    if (option == selected) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSelect(option)
                        keyboardController?.hide()
                        coroutineScope.launch {
                            onClose()
                        }
                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedScreenPreview() {
    FeedScreenContent(uiState = FeedUiState(subscriptions = listOf(
        SubscriptionItem(
            channelId = "cid1",
            title = "Sample Channel 1"
        ),
        SubscriptionItem(
            channelId = "cid2",
            title = "Sample Channel 2"
        )
    )))
}

@Preview(showBackground = true, name = "Empty Feed")
@Composable
fun FeedScreenEmptyPreview() {
    FeedScreenContent(uiState = FeedUiState(subscriptions = emptyList()))
}