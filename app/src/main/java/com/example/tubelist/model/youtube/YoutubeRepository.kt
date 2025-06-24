package com.example.tubelist.model.youtube

import com.example.tubelist.app.IDispatcherProvider
import kotlinx.coroutines.withContext

class YoutubeRepository(
    private val api: YoutubeApiService,
    private val dispatcher: IDispatcherProvider
) : IYoutubeRepository {
    override suspend fun getSubscriptions(): List<SubscriptionItem> {
        return withContext(dispatcher.io) {
            val response =  api.getSubscriptions()
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