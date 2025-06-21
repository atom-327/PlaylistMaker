package com.practicum.playlistmaker.core.data

import android.content.SharedPreferences
import com.practicum.playlistmaker.core.domain.api.SharedPreferencesRepository

class SharedPreferencesRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    SharedPreferencesRepository {

    override fun getStrItem(itemId: String): String? {
        return sharedPreferences.getString(itemId, null)
    }

    override fun getBooleanItem(itemId: String): Boolean {
        return sharedPreferences.getBoolean(itemId, true)
    }

    override fun putStrItem(itemId: String, value: String) {
        sharedPreferences.edit().putString(itemId, value).apply()
    }

    override fun putBooleanItem(itemId: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(itemId, value).apply()
    }

    override fun removeItem(itemId: String) {
        sharedPreferences.edit().remove(itemId).apply()
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }
}
