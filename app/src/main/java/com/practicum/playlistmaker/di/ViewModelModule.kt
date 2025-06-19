package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.presentation.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.presentation.view_model.SearchViewModel
import com.practicum.playlistmaker.settings.presentation.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { (tracksInteractor: TracksInteractor, searchHistoryInteractor: SearchHistoryInteractor, errorStr: String, emptyStr: String) ->
        SearchViewModel(tracksInteractor, searchHistoryInteractor, errorStr, emptyStr)
    }

    viewModel { (playerInteractor: PlayerInteractor, trackUrl: String) ->
        AudioPlayerViewModel(playerInteractor, trackUrl)
    }

    viewModel {
        SettingsViewModel(get(), get())
    }
}
