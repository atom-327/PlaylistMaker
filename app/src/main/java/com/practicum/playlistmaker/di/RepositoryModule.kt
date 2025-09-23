package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.core.data.SharedPreferencesRepositoryImpl
import com.practicum.playlistmaker.core.data.mapper.DataMapperImpl
import com.practicum.playlistmaker.core.domain.api.DataMapper
import com.practicum.playlistmaker.core.domain.api.SharedPreferencesRepository
import com.practicum.playlistmaker.media.data.FavouritesRepositoryImpl
import com.practicum.playlistmaker.media.data.PlaylistsRepositoryImpl
import com.practicum.playlistmaker.media.data.mapper.AddedTrackDbConvertor
import com.practicum.playlistmaker.media.data.mapper.PlaylistDbConvertor
import com.practicum.playlistmaker.media.data.mapper.TrackDbConvertor
import com.practicum.playlistmaker.media.domain.api.FavouritesRepository
import com.practicum.playlistmaker.media.domain.api.PlaylistsRepository
import com.practicum.playlistmaker.player.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.search.data.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    factory<SharedPreferencesRepository> {
        SharedPreferencesRepositoryImpl(get())
    }

    single<DataMapper> {
        DataMapperImpl(get())
    }

    factory<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get(), get())
    }

    factory<SettingsRepository> {
        SettingsRepositoryImpl(androidContext())
    }

    factory<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    factory { TrackDbConvertor() }

    factory { PlaylistDbConvertor() }

    factory { AddedTrackDbConvertor() }

    single<FavouritesRepository> {
        FavouritesRepositoryImpl(get(), get())
    }

    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(get(), get(), get(), get(), get())
    }
}
