package com.example.tubelist.model.youtube

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface YoutubeApiService {
    @GET("youtube/v3/subscriptions")
    suspend fun getSubscriptions(
        @Query("part") part: String = "snippet",
        @Query("mine") mine: Boolean = true,
        @Query("maxResults") maxResults: Int = 50,
        @Header("Authorization") authHeader: String
    ): SubscriptionResponse

    @GET("youtube/v3/channels")
    suspend fun getChannel(
        @Query("part") part: String = "snippet,contentDetails,statistics",
        @Query("id") channelId: String,
        @Header("Authorization") authHeader: String
    ): ChannelResponse
}