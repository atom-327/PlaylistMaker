package com.practicum.playlistmaker.core.domain.api

import android.content.SharedPreferences

interface SharedPreferencesRepository {
    fun getStrItem(itemId: String): String?
    fun getBooleanItem(itemId: String): Boolean
    fun putStrItem(itemId: String, value: String)
    fun putBooleanItem(itemId: String, value: Boolean)
    fun removeItem(itemId: String)
    fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener)
}
