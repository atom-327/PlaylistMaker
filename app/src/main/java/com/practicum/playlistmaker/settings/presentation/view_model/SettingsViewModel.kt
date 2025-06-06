package com.practicum.playlistmaker.settings.presentation.view_model

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.ui.App

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(this[APPLICATION_KEY] as App)
            }
        }
    }

    private val settingsInteractor = Creator.provideSettingsInteractor(getApplication())
    private val sharingInteractor = Creator.provideSharingInteractor()

    fun getThemeSettings(): Boolean {
        return settingsInteractor.getThemeSettings()
    }

    fun updateThemeSetting(settings: Boolean) {
        settingsInteractor.updateThemeSetting(settings)
    }

    fun shareApp(): Intent {
        return sharingInteractor.shareApp()
    }

    fun openTerms(): Intent {
        return sharingInteractor.openTerms()
    }

    fun openSupport(): Intent {
        return sharingInteractor.openSupport()
    }
}
