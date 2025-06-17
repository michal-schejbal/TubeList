package com.ginoskos.console.application

interface ISettings {
    operator fun contains(key: String): Boolean

    fun remove(key: String)

    fun putBoolean(key: String, value: Boolean)

    fun putInteger(key: String, value: Int)

    fun putLong(key: String, value: Long)

    fun putString(key: String, value: String?)

    fun getBoolean(key: String): Boolean?

    fun getBoolean(key: String, def: Boolean?): Boolean?

    fun getInteger(key: String): Int?

    fun getInteger(key: String, def: Int?): Int?

    fun getLong(key: String): Long?

    fun getLong(key: String, def: Long?): Long?

    fun getString(key: String): String?

    fun getString(key: String, def: String?): String?
}
