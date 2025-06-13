package com.practicum.playlistmaker.settings.presentation.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.creator.Creator
import com.practicum.playlistmaker.core.ui.App
import com.practicum.playlistmaker.settings.presentation.SettingsState
import com.practicum.playlistmaker.sharing.domain.models.EmailData

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

    private val settingsInteractor = Creator.provideSettingsInteractor()
    private val sharingInteractor = Creator.provideSharingInteractor()

    private val state =
        MutableLiveData(SettingsState(null, settingsInteractor.getThemeSettings()))

    fun getState(): LiveData<SettingsState> = state

    fun updateThemeSettings(checked: Boolean) {
        settingsInteractor.updateThemeSetting(checked)
        state.value = state.value?.copy(isDarkTheme = settingsInteractor.getThemeSettings())
    }

    fun shareState() {
        state.value = state.value?.copy(state = sharingInteractor.shareApp(getShareAppLink()))
    }

    fun supportingState() {
        state.value =
            state.value?.copy(state = sharingInteractor.openSupport(getSupportEmailData()))
    }

    fun agreementState() {
        state.value = state.value?.copy(state = sharingInteractor.openTerms(getTermsLink()))
    }

    private fun getShareAppLink(): String {
        return getApplication<App>().getString(R.string.messageToShareApp)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            getApplication<App>().getString(R.string.email),
            getApplication<App>().getString(R.string.subjectOfMessage),
            getApplication<App>().getString(R.string.messageToSupport)
        )
    }

    private fun getTermsLink(): String {
        return getApplication<App>().getString(R.string.agreementLink)
    }
}
