package com.example.tubelist.app

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import kotlin.coroutines.cancellation.CancellationException

fun String.formatAsPublishedDate(): String {
    return try {
        val parsed = ZonedDateTime.parse(this)
        val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())
        parsed.format(formatter)
    } catch (e: DateTimeParseException) {
        ""
    }
}

inline fun <T> runCatchingCancellable(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        Result.failure(e)
    }
}