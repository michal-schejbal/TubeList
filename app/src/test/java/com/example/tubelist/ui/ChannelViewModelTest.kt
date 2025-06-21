package com.example.tubelist.ui

import com.example.tubelist.AbstractBaseTest
import com.example.tubelist.model.youtube.Channel
import com.example.tubelist.model.youtube.IYoutubeRepository
import com.example.tubelist.ui.screens.ChannelViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject

@OptIn(ExperimentalCoroutinesApi::class)
class ChannelViewModelTest : AbstractBaseTest() {
    private val repository: IYoutubeRepository by inject(IYoutubeRepository::class.java)
    private lateinit var viewModel: ChannelViewModel

    @Before
    fun before() {
        viewModel = ChannelViewModel(repository)
    }

    @After
    fun after() {
    }

    @Test
    fun `Fetch channel success updates state`() = runTest {
        val channelId = "abc123"
        val expected = Channel(
            channelId = channelId,
            title = "Test Channel",
            description = "Test Desc",
            published = "2001-01-01T01:01:01Z"
        )
        TestYoutubeRepositoryConfig.channelToReturn = expected

        viewModel.getChannel(channelId)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(expected, state.channel)
        assertNull(state.error)
    }

    @Test
    fun `Fetch channel with same ID does not fetch again`() = runTest {
        val channelId = "cachedId"
        val expectedChannel = Channel(
            channelId = channelId,
            title = "Cached Channel",
            description = "This channel should be fetched only once.",
            published = "2024-01-01T01:01:01Z"
        )
        TestYoutubeRepositoryConfig.channelToReturn = expectedChannel

        logger.d("First fetch")
        viewModel.getChannel(channelId)
        advanceUntilIdle()

        assertEquals(1, TestYoutubeRepositoryConfig.callCount)
        assertEquals(channelId, TestYoutubeRepositoryConfig.requestedChannelId)

        logger.d("Second fetch")
        viewModel.getChannel(channelId)
        advanceUntilIdle()

        assertEquals(1, TestYoutubeRepositoryConfig.callCount)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(expectedChannel, state.channel)
    }
}