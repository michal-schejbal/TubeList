package com.example.tubelist.app

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

fun String.formatAsPublishedDate(): String {
    return try {
        val parsed = ZonedDateTime.parse(this)
        val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())
        parsed.format(formatter)
    } catch (e: DateTimeParseException) {
        ""
    }
}
