package com.example.tubelist.ui

import com.example.tubelist.app.logger.IAppLogger
import com.example.tubelist.model.youtube.Channel
import com.example.tubelist.model.youtube.IYoutubeRepository
import com.example.tubelist.model.youtube.SubscriptionItem
import com.example.tubelist.ui.TestYoutubeRepositoryConfig.callCount
import com.example.tubelist.ui.TestYoutubeRepositoryConfig.channelToReturn
import com.example.tubelist.ui.TestYoutubeRepositoryConfig.requestedChannelId
import org.koin.java.KoinJavaComponent.inject

object TestYoutubeRepositoryConfig {
    var channelToReturn: Channel? = null
    var requestedChannelId: String? = null
    var callCount: Int = 0

    fun reset() {
        channelToReturn = null
        requestedChannelId = null
        callCount = 0
    }
}

class TestYoutubeRepository : IYoutubeRepository {
    private val logger: IAppLogger by inject(IAppLogger::class.java)

    override suspend fun getSubscriptions(): List<SubscriptionItem> {
        return emptyList()
    }

    override suspend fun getChannel(channelId: String): Channel? {
        requestedChannelId = channelId
        callCount++
        logger.d("Channel requested: $channelId, calls ${callCount}x")
        return channelToReturn
    }
}