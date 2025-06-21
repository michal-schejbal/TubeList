package com.example.tubelist.app

enum class LogLevel(val priority: Int) {
    VERBOSE(android.util.Log.VERBOSE),
    DEBUG(android.util.Log.DEBUG),
    INFO(android.util.Log.INFO),
    WARN(android.util.Log.WARN),
    ERROR(android.util.Log.ERROR),
    ASSERT(android.util.Log.ASSERT),
    WTF(android.util.Log.ASSERT)
}

interface IAppLogger {
    fun v(message: String, vararg args: Any?)
    fun d(message: String, vararg args: Any?)
    fun i(message: String, vararg args: Any?)
    fun w(message: String, vararg args: Any?)
    fun e(throwable: Throwable? = null, message: String, vararg args: Any?)
    fun wtf(throwable: Throwable? = null, message: String, vararg args: Any?)
    fun log(level: LogLevel, message: String, vararg args: Any?)
}