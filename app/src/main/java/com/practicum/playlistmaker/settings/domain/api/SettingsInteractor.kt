package com.practicum.playlistmaker.settings.domain.api

interface SettingsInteractor {
    fun getThemeSettings(): Boolean
    fun updateThemeSetting(settings: Boolean)
}
