package com.example.tubelist.app

import com.example.tubelist.app.logger.IAppLogger
import com.example.tubelist.app.logger.TimberLogger
import com.example.tubelist.app.tokens.ITokenStorage
import com.example.tubelist.app.tokens.TokenStorage
import com.example.tubelist.model.auth.AuthManager
import com.example.tubelist.model.auth.AuthViewModel
import com.example.tubelist.model.auth.IAuthManager
import com.example.tubelist.model.user.IUserRepository
import com.example.tubelist.model.user.UserRepository
import com.example.tubelist.model.youtube.IYoutubeRepository
import com.example.tubelist.model.youtube.YoutubeApiService
import com.example.tubelist.model.youtube.YoutubeRepository
import com.example.tubelist.ui.screens.ChannelViewModel
import com.example.tubelist.ui.screens.FeedViewModel
import com.example.tubelist.ui.screens.ProfileViewModel
import com.ginoskos.console.application.ISettings
import com.ginoskos.console.application.Settings
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Modules {
     val items = listOf(
        module {
            single<IAppLogger> { TimberLogger }
            single<ISettings> { Settings(androidContext()) }
            single<ITokenStorage> { TokenStorage }
            single<IDispatcherProvider> { AppDispatcherProvider }
            single<Retrofit> {
                Retrofit.Builder()
                    .baseUrl(Config.GOOGLE_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            single<YoutubeApiService> { get<Retrofit>().create(YoutubeApiService::class.java) }
        },
        module {
            single<IAuthManager> { AuthManager() }
            viewModel { AuthViewModel(get()) }
        },
        module {
            factory<IUserRepository> { UserRepository(androidContext()) }
            viewModel { ProfileViewModel(get(), get()) }
        },
        module {
            factory<IYoutubeRepository> { YoutubeRepository(api = get(), tokenStorage = get(), dispatcher = get()) }
            viewModel { FeedViewModel(get()) }
            viewModel { ChannelViewModel(get()) }
        },
    )
}