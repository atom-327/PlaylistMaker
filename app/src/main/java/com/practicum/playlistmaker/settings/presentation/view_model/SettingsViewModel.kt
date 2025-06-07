package com.practicum.playlistmaker.settings.presentation.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.core.ui.App

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    companion object {
        private const val STATE_DEFAULT = 0

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(this[APPLICATION_KEY] as App)
            }
        }
    }

    private var stateCount = STATE_DEFAULT

    private val state = MutableLiveData(stateCount)
    fun getState(): LiveData<Int> = state

    fun renderState(state: Int) {
        this.state.postValue(state)
    }
}
