package com.practicum.playlistmaker.settings.data

import android.content.Context
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.ui.App

class SettingsRepositoryImpl(private val applicationContext: Context) : SettingsRepository {
    override fun getThemeSettings(): Boolean {
        return (applicationContext as App).getAppTheme()
    }

    override fun updateThemeSetting(settings: Boolean) {
        (applicationContext as App).switchTheme(settings)
    }
}
