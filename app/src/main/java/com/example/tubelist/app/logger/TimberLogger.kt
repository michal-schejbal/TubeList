package com.example.tubelist.app.logger

import timber.log.Timber

object TimberLogger : IAppLogger {
    init {
        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String {
                val className = element.className.substringAfterLast('.').substringBefore('$')
                return "$className:${element.lineNumber}"
            }

            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                val realElement = Throwable().stackTrace.firstOrNull {
                    it.className != TimberLogger::class.java.name
                }

                val actualTag = realElement?.let {
                    "${it.className.substringAfterLast('.')}:${it.lineNumber}"
                } ?: tag ?: "unknown"

                super.log(priority, actualTag, message, t)
            }

        })
    }

    override fun v(message: String, vararg args: Any?) = Timber.v(message, *args)
    override fun d(message: String, vararg args: Any?) = Timber.d(message, *args)
    override fun i(message: String, vararg args: Any?) = Timber.i(message, *args)
    override fun w(message: String, vararg args: Any?) = Timber.w(message, *args)
    override fun e(throwable: Throwable?, message: String, vararg args: Any?) =
        Timber.e(throwable, message, *args)
    override fun wtf(throwable: Throwable?, message: String, vararg args: Any?) =
        Timber.wtf(throwable, message, *args)
    override fun log(level: LogLevel, message: String, vararg args: Any?) {
        when (level) {
            LogLevel.VERBOSE -> v(message, *args)
            LogLevel.DEBUG -> d(message, *args)
            LogLevel.INFO -> i(message, *args)
            LogLevel.WARN -> w(message, *args)
            LogLevel.ERROR -> e(null, message, *args)
            LogLevel.ASSERT -> wtf(null, message, *args)
            LogLevel.WTF -> wtf(null, message, *args)
        }
    }
}