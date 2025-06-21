package com.practicum.playlistmaker.settings.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.presentation.SettingsState
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val state = MutableLiveData(SettingsState(settingsInteractor.getThemeSettings()))
    fun getState(): LiveData<SettingsState> = state

    fun updateThemeSettings(checked: Boolean) {
        settingsInteractor.updateThemeSetting(checked)
        state.value = state.value?.copy(isDarkTheme = settingsInteractor.getThemeSettings())
    }

    fun shareState() {
        sharingInteractor.shareApp()
    }

    fun supportingState() {
        sharingInteractor.openSupport()
    }

    fun agreementState() {
        sharingInteractor.openTerms()
    }
}
