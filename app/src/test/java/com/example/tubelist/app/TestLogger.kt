package com.example.tubelist.app

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TestLogger : IAppLogger {
    private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)

    override fun v(message: String, vararg args: Any?) =
        log(LogLevel.VERBOSE, message, null, *args)
    override fun d(message: String, vararg args: Any?) =
        log(LogLevel.DEBUG, message, null, *args)
    override fun i(message: String, vararg args: Any?) =
        log(LogLevel.INFO, message, null, *args)
    override fun w(message: String, vararg args: Any?) =
        log(LogLevel.WARN, message, null, *args)
    override fun e(throwable: Throwable?, message: String, vararg args: Any?) =
        log(LogLevel.ERROR, message, throwable, *args)
    override fun wtf(throwable: Throwable?, message: String, vararg args: Any?) =
        log(LogLevel.WTF, message, throwable, *args)
    override fun log(level: LogLevel, message: String, vararg args: Any?) =
        log(level, message, null, *args)

    private fun log(level: LogLevel, message: String, throwable: Throwable?, vararg args: Any?) {
        val formattedMessage = try {
            message.format(*args)
        } catch (e: Exception) {
            "LOG_FORMAT_ERROR: \"$message\" with args=${args.toList()}"
        }

        val timestamp = timeFormat.format(Date())
        val thread = Thread.currentThread().name

        val traceElement = Throwable().stackTrace.firstOrNull {
            !it.fileName.startsWith("TestLogger")
        }

        val location = traceElement?.let {
            val rawClass = it.className
            val classOnly = rawClass.substringAfterLast('.').substringBefore('$')
            "$classOnly:${it.lineNumber}"
        } ?: "unknown:0"

        val tag = traceElement?.className?.substringBeforeLast('.', "app") ?: "app"

        val levelChar = when (level) {
            LogLevel.VERBOSE -> "V"
            LogLevel.DEBUG -> "D"
            LogLevel.INFO -> "I"
            LogLevel.WARN -> "W"
            LogLevel.ERROR -> "E"
            LogLevel.ASSERT, LogLevel.WTF -> "A"
        }

        val logLine = "$timestamp $thread  $tag  $levelChar  $location: $formattedMessage"
        println(logLine)

        throwable?.let {
            println("$timestamp $thread  $tag  $levelChar  $location: ${it.stackTraceToString()}")
        }
    }
}