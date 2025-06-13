package com.practicum.playlistmaker.settings.presentation

import android.content.Intent

data class SettingsState(
    val state: Intent?, val isDarkTheme: Boolean
)
