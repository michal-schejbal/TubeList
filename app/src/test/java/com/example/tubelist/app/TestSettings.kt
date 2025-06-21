package com.example.tubelist.app

import com.ginoskos.console.application.ISettings
import com.google.gson.GsonBuilder

class TestSettings : ISettings {
    companion object {
        val data: MutableMap<String, String> = mutableMapOf()
    }

    override fun contains(key: String): Boolean {
        return data.containsKey(key)
    }

    override fun remove(key: String) {
        data.remove(key)
    }

    override fun putBoolean(key: String, value: Boolean) {
        data[key] = GsonBuilder().create().toJson(value)
    }

    override fun putInteger(key: String, value: Int) {
        data[key] = GsonBuilder().create().toJson(value)
    }

    override fun putLong(key: String, value: Long) {
        data[key] = GsonBuilder().create().toJson(value)
    }

    override fun putString(key: String, value: String?) {
        data[key] = GsonBuilder().create().toJson(value)
    }

    override fun getBoolean(key: String): Boolean? {
        return getBoolean(key, null)
    }

    override fun getBoolean(key: String, def: Boolean?): Boolean? {
        if (!contains(key)) {
            return null
        }
        return GsonBuilder().create().fromJson(data[key], Boolean::class.java) ?: false
    }

    override fun getInteger(key: String): Int? {
        return getInteger(key, null)
    }

    override fun getInteger(key: String, def: Int?): Int? {
        if (!contains(key)) {
            return null
        }
        return GsonBuilder().create().fromJson(data[key], Int::class.java) ?: -1
    }

    override fun getLong(key: String): Long? {
        return getLong(key, null)
    }

    override fun getLong(key: String, def: Long?): Long? {
        if (!contains(key)) {
            return def
        }
        return GsonBuilder().create().fromJson(data[key], Long::class.java) ?: -1
    }

    override fun getString(key: String): String? {
        return getString(key, null)
    }

    override fun getString(key: String, def: String?): String? {
        if (!contains(key)) {
            return def
        }
        return GsonBuilder().create().fromJson(data[key], String::class.java) ?: null
    }
}