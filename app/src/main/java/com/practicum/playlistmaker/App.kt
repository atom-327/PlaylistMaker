package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    companion object {
        const val TEXT_KEY = "isDarkTheme"
        const val TRACK_ID = "TRACK_ID"
    }

    private lateinit var sharedPreferences: SharedPreferences
    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)
        sharedPreferences = Creator.createSharedPreferences()
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
