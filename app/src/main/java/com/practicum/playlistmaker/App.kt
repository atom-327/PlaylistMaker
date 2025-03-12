package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.SettingsActivity.Companion.TEXT_KEY
import com.practicum.playlistmaker.SettingsActivity.Companion.SHARED_PREF

class App : Application() {

    private lateinit var sharedPreferences: SharedPreferences
    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE)
        darkTheme = getAppTheme()
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        sharedPreferences.edit().putBoolean(TEXT_KEY, darkTheme).apply()
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun getAppTheme(): Boolean {
        return sharedPreferences.getBoolean(TEXT_KEY, false)
    }
}
