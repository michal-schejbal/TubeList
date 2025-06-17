package com.example.tubelist.model.youtube

data class SubscriptionItem(
    val channelId: String,
    val title: String,
    val thumbnails: Thumbnails? = null
)

data class Channel(
    val channelId: String,
    val published: String,
    val title: String,
    val description: String,
    val thumbnails: Thumbnails? = null
)


interface IYoutubeRepository {
    suspend fun getSubscriptions(): List<SubscriptionItem>
    suspend fun getChannel(channelId: String): Channel?
}