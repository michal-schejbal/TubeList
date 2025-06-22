package com.example.tubelist.app

import kotlinx.coroutines.Dispatchers

object AppDispatcherProvider : IDispatcherProvider {
    override val io = Dispatchers.IO
    override val main = Dispatchers.Main
    override val default = Dispatchers.Default
}