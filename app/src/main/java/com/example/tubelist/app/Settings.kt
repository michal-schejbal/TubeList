package com.ginoskos.console.application

import android.content.Context
import android.content.SharedPreferences


class Settings(private val context: Context) : ISettings {

    override fun contains(key: String): Boolean {
        return preferences().contains(key)
    }

    override fun remove(key: String) {
        editor().remove(key).apply()
    }

    override fun putBoolean(key: String, value: Boolean) {
        editor().putBoolean(key, value).apply()
    }

    override fun putInteger(key: String, value: Int) {
        editor().putInt(key, value).apply()
    }

    override fun putLong(key: String, value: Long) {
        editor().putLong(key, value).apply()
    }

    override fun putString(key: String, value: String?) {
        editor().putString(key, value).apply()
    }

    override fun getBoolean(key: String): Boolean? {
        if (!preferences().contains(key)) {
            return null
        }
        return preferences().getBoolean(key, false)
    }

    override fun getBoolean(key: String, def: Boolean?): Boolean? {
        if (!preferences().contains(key)) {
            return def
        }
        return try {
            preferences().getBoolean(key, false)
        } catch (e: ClassCastException) {
            def
        }
    }

    override fun getInteger(key: String): Int? {
        return getInteger(key, null)
    }

    override fun getInteger(key: String, def: Int?): Int? {
        if (!preferences().contains(key)) {
            return def
        }
        return try {
            val result = preferences().getInt(key, -1)
            if (result == -1) {
                def
            } else {
                result
            }
        } catch (e: ClassCastException) {
            def
        }
    }

    override fun getLong(key: String): Long? {
        return getLong(key, null)
    }

    override fun getLong(key: String, def: Long?): Long? {
        if (!preferences().contains(key)) {
            return def
        }
        return try {
            val result = preferences().getLong(key, -1)
            if (result == -1L) {
                def
            } else {
                result
            }
        } catch (e: ClassCastException) {
            def
        }
    }

    override fun getString(key: String): String? {
        return getString(key, null)
    }

    override fun getString(key: String, def: String?): String? {
        if (!preferences().contains(key)) {
            return def
        }
        return try {
            preferences().getString(key, def)
        } catch (e: ClassCastException) {
            def
        }
    }

    private fun preferences(): SharedPreferences {
        return context.getSharedPreferences(
            context.packageName + ".pref", Context.MODE_PRIVATE)
    }

    private fun editor(): SharedPreferences.Editor {
        return preferences().edit()
    }
}

