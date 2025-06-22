package com.example.tubelist.model.youtube

import com.example.tubelist.app.IDispatcherProvider
import com.example.tubelist.app.tokens.ITokenStorage
import kotlinx.coroutines.withContext

class YoutubeRepository(
    private val api: YoutubeApiService,
    private val tokenStorage: ITokenStorage,
    private val dispatcher: IDispatcherProvider
) : IYoutubeRepository {
    override suspend fun getSubscriptions(): List<SubscriptionItem> {
        return withContext(dispatcher.io) {
            val response =  api.getSubscriptions(authHeader = "Bearer ${tokenStorage.getAccessToken()}")
            response.items.map {
                SubscriptionItem(
                    title = it.snippet.title,
                    thumbnails = it.snippet.thumbnails,
                    channelId = it.snippet.resourceId.channelId
                )
            }
        }
    }

    override suspend fun getChannel(channelId: String): Channel? {
        return withContext(dispatcher.io) {
            val response = api.getChannel(
                channelId = channelId,
                authHeader = "Bearer ${tokenStorage.getAccessToken()}"
            )
            val snippet = response.items.firstOrNull()?.snippet
            if (snippet != null) {
                Channel(
                    title = snippet.title,
                    published = snippet.publishedAt,
                    description = snippet.description,
                    thumbnails = snippet.thumbnails,
                    channelId = channelId,
                )
            } else {
                null
            }
        }
    }
}