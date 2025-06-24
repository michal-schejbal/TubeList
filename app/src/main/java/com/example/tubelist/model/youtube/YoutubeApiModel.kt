package com.example.tubelist.model.youtube

data class Thumbnail(
    val url: String?,
    val width: Int? = null,
    val height: Int? = null
)

data class Thumbnails(
    val default: Thumbnail? = null,
    val medium: Thumbnail? = null,
    val high: Thumbnail? = null
)

/**
 * SubscriptionResponse DTOs
 */
data class SubscriptionResponse(
    val items: List<SubscriptionResponseItem>
)

data class SubscriptionResponseItem(
    val snippet: SubscriptionResponseSnippet
)

data class SubscriptionResponseSnippet(
    val title: String,
    val thumbnails: Thumbnails,
    val resourceId: SubscriptionResponseResourceId
)

data class SubscriptionResponseResourceId(val channelId: String)


/**
 * ChannelResponse DTOs
 */
data class ChannelResponse(
    val items: List<ChannelResponseItem>
)

data class ChannelResponseItem(
    val id: String,
    val snippet: ChannelResponseSnippet,
    val contentDetails: ChannelResponseItemContentDetails,
    val statistics: ChannelResponseItemStatistics
)

data class ChannelResponseSnippet(
    val title: String,
    val publishedAt: String,
    val description: String,
    val thumbnails: Thumbnails
)

data class ChannelResponseItemContentDetails(
    val relatedPlaylists: ChannelResponseItemRelatedPlaylists
)

data class ChannelResponseItemRelatedPlaylists(
    val uploads: String
)

data class ChannelResponseItemStatistics(
    val viewCount: String,
    val subscriberCount: String,
    val hiddenSubscriberCount: Boolean,
    val videoCount: String
)