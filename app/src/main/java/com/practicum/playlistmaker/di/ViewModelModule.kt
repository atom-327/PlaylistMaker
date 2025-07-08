package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media.presentation.view_model.FavoritesViewModel
import com.practicum.playlistmaker.media.presentation.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.player.presentation.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.presentation.view_model.SearchViewModel
import com.practicum.playlistmaker.settings.presentation.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { (errorStr: String, emptyStr: String) ->
        SearchViewModel(get(), get(), get(), errorStr, emptyStr)
    }

    viewModel {
        AudioPlayerViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoritesViewModel()
    }

    viewModel {
        PlaylistsViewModel()
    }
}
