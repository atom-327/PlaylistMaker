package com.practicum.playlistmaker.settings.presentation.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(this[APPLICATION_KEY] as Application)
            }
        }

//        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                val sharingInteractor = Creator.provideSharingInteractor(context)
//                val settingsInteractor = Creator.provideSettingsInteractor(context)
//
//                SettingsViewModel(settingsInteractor, sharingInteractor)
//            }
//        }
    }

    private val settingsInteractor = Creator.provideSettingsInteractor(getApplication())
    private val sharingInteractor = Creator.provideSharingInteractor(getApplication())

    fun getThemeSettings(): Boolean {
        return settingsInteractor.getThemeSettings()
    }

    fun updateThemeSetting(settings: Boolean) {
        settingsInteractor.updateThemeSetting(settings)
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }
}
