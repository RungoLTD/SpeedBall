package com.rungo.speedball.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class SharedPreferencesProvider(
    applicationContext: Context
) {
    private var sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
    private var sharedPreferencesEditor: SharedPreferences.Editor? = null

    private fun remove(key: String) {
        sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor?.remove(key)
        sharedPreferencesEditor?.apply()
    }

    fun set(key: String, value: String?) {
        if (value == null) remove(key) else {
            sharedPreferencesEditor = sharedPreferences.edit()
            sharedPreferencesEditor?.putString(key, value)
            sharedPreferencesEditor?.apply()
        }
    }

    fun set(key: String, value: Int?) {
        if (value == null) remove(key) else {
            sharedPreferencesEditor = sharedPreferences.edit()
            sharedPreferencesEditor?.putInt(key, value)
            sharedPreferencesEditor?.apply()
        }
    }

    fun set(key: String, value: Long?) {
        if (value == null) remove(key) else {
            sharedPreferencesEditor = sharedPreferences.edit()
            sharedPreferencesEditor?.putLong(key, value)
            sharedPreferencesEditor?.apply()
        }
    }

    fun set(key: String, value: Boolean?) {
        if (value == null) remove(key) else {
            sharedPreferencesEditor = sharedPreferences.edit()
            sharedPreferencesEditor?.putBoolean(key, value)
            sharedPreferencesEditor?.apply()
        }
    }

    fun set(key: String, value: Float?) {
        if (value == null) remove(key) else {
            sharedPreferencesEditor = sharedPreferences.edit()
            sharedPreferencesEditor?.putFloat(key, value)
            sharedPreferencesEditor?.apply()
        }
    }

    fun getString(key: String): String? {
        return if (!sharedPreferences.contains(key)) null else
            sharedPreferences.getString(key, null)
    }

    fun getSensitive(key: String): Int? {
        return if (!sharedPreferences.contains(key)) null else
            sharedPreferences.getInt(key, 50)
    }

    fun getInt(key: String): Int? {
        return if (!sharedPreferences.contains(key)) null else
            sharedPreferences.getInt(key, 0)
    }

    fun getLong(key: String): Long? {
        return if (!sharedPreferences.contains(key)) null else
            sharedPreferences.getLong(key, 0)
    }

    fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun getFloat(key: String): Float? {
        return if (!sharedPreferences.contains(key)) null else
            sharedPreferences.getFloat(key, 0f)
    }
}