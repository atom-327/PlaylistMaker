package com.practicum.playlistmaker.settings.domain.api

interface SettingsRepository {
    fun getThemeSettings(): Boolean
    fun updateThemeSetting(settings: Boolean)
}
