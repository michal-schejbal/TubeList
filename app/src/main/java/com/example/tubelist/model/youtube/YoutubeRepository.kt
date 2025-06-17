package com.example.tubelist.model.youtube

import com.example.tubelist.app.ITokenStorage

class YoutubeRepository(private val api: YoutubeApiService, private val tokenStorage: ITokenStorage) :
    IYoutubeRepository {
    override suspend fun getSubscriptions(): List<SubscriptionItem> {
        val response = api.getSubscriptions(authHeader = "Bearer ${tokenStorage.getAccessToken()}")
        return response.items.map {
            SubscriptionItem(
                title = it.snippet.title,
                thumbnails = it.snippet.thumbnails,
                channelId = it.snippet.resourceId.channelId
            )
        }
    }

    override suspend fun getChannel(channelId: String): Channel? {
        val response = api.getChannel(
            channelId = channelId,
            authHeader = "Bearer ${tokenStorage.getAccessToken()}"
        )
        val snippet = response.items.firstOrNull()?.snippet
        return if (snippet != null) {
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