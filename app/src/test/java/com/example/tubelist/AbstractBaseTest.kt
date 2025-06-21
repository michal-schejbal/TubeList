package com.example.tubelist

import com.example.tubelist.app.IAppLogger
import com.example.tubelist.app.TestLogger
import com.example.tubelist.app.TestSettings
import com.example.tubelist.model.youtube.IYoutubeRepository
import com.example.tubelist.ui.TestYoutubeRepository
import com.example.tubelist.ui.TestYoutubeRepositoryConfig
import com.example.tubelist.ui.screens.ChannelViewModel
import com.example.tubelist.ui.screens.FeedViewModel
import com.ginoskos.console.application.ISettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import org.koin.test.KoinTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@OptIn(ExperimentalCoroutinesApi::class)
abstract class AbstractBaseTest : KoinTest {
    val logger: IAppLogger by inject(IAppLogger::class.java)
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    open fun setup() {
        startKoin {
            modules(
                module {
                    single<IAppLogger> { TestLogger }
                    single<ISettings> { TestSettings() }
                },
                module {
                    factory<IYoutubeRepository> { TestYoutubeRepository() }
                    viewModel { FeedViewModel(get()) }
                    viewModel { ChannelViewModel(get()) }
                }
            )
        }

        Dispatchers.setMain(testDispatcher)

        TestYoutubeRepositoryConfig.reset()
    }


    @AfterTest
    open fun conclude() {
        Dispatchers.resetMain()
        stopKoin()
    }
}