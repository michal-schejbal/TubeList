package com.example.tubelist.app

import kotlinx.coroutines.CoroutineDispatcher

class TestDispatcherProvider(private val testDispatcher: CoroutineDispatcher) : IDispatcherProvider {
    override val io = testDispatcher
    override val main = testDispatcher
    override val default = testDispatcher
}