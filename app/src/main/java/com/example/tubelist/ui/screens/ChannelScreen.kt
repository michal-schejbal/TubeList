package com.example.tubelist.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tubelist.app.formatAsPublishedDate
import com.example.tubelist.model.youtube.Channel
import com.example.tubelist.ui.components.LoadingComponent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChannelScreen(
    modifier: Modifier = Modifier,
    channelId: String,
    viewModel: ChannelViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(channelId) {
        viewModel.getChannel(channelId)
    }

    ChannelScreenContent(modifier, state)
}

@Composable
internal fun ChannelScreenContent(
    modifier: Modifier = Modifier,
    state: ChannelUiState
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            state.isLoading -> LoadingComponent()

            state.error != null -> Text(
                text = "Error: ${state.error}",
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )

            state.channel != null -> {
                val channel = state.channel
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .padding(16.dp)
                ) {
                    if (channel.thumbnails != null) {
                        AsyncImage(
                            model = channel.thumbnails.high?.url,
                            contentDescription = channel.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Subscriptions,
                            contentDescription = channel.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            tint = Color.DarkGray
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(channel.title, style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Published on ${channel.published.formatAsPublishedDate()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(channel.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChannelScreenPreview() {
    ChannelScreenContent(
        state = ChannelUiState(channel = Channel(
            channelId = "cid",
            published = "2001-01-01T01:01:01Z",
            title = "Sample Channel",
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
        ))
    )
}