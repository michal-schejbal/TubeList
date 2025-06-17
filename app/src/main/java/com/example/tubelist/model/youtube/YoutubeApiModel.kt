package com.example.tubelist.model.youtube

import com.google.gson.annotations.SerializedName

data class SubscriptionResponse(
    @SerializedName("items") val items: List<SubscriptionItemDto>
)

data class SubscriptionItemDto(
    @SerializedName("snippet") val snippet: SnippetDto
)

data class SnippetDto(
    val title: String,
    val thumbnails: Thumbnails,
    val resourceId: ResourceIdDto
)

data class Thumbnail(
    val url: String?,
    val width: Int? = null,
    val height: Int? = null
)

data class ResourceIdDto(val channelId: String)



data class ChannelResponse(
    val items: List<ChannelItem>
)

data class ChannelItem(
    val id: String,
    val snippet: ChannelSnippet,
    val contentDetails: ChannelContentDetails,
    val statistics: ChannelStatistics
)

data class ChannelSnippet(
    val title: String,
    val publishedAt: String,
    val description: String,
    val thumbnails: Thumbnails
)

data class Thumbnails(
    val default: Thumbnail? = null,
    val medium: Thumbnail? = null,
    val high: Thumbnail? = null
)

data class ChannelContentDetails(
    val relatedPlaylists: RelatedPlaylists
)

data class RelatedPlaylists(
    val uploads: String
)

data class ChannelStatistics(
    val viewCount: String,
    val subscriberCount: String,
    val hiddenSubscriberCount: Boolean,
    val videoCount: String
)