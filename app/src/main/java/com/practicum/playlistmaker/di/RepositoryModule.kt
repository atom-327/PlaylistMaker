package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.core.data.SharedPreferencesRepositoryImpl
import com.practicum.playlistmaker.core.data.mapper.DataMapperImpl
import com.practicum.playlistmaker.core.domain.api.DataMapper
import com.practicum.playlistmaker.core.domain.api.SharedPreferencesRepository
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
        PlayerRepositoryImpl(get())
    }

    factory<SettingsRepository> {
        SettingsRepositoryImpl(androidContext())
    }

    factory<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }
}
