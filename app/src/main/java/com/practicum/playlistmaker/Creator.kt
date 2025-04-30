package com.practicum.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.google.gson.Gson
import com.practicum.playlistmaker.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.data.SharedPreferencesImpl
import com.practicum.playlistmaker.data.TracksRepositoryImpl
import com.practicum.playlistmaker.data.mapper.DataMapper
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.api.DataMapperRepository
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.api.PlayerRepository
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.SharedPreferencesRepository
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.PlayerImpl
import com.practicum.playlistmaker.domain.impl.SearchHistoryImpl
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {

    private lateinit var app: Application
    private const val SHARED_PREF = "ThemePrefs"

    fun initApplication(app: Application) {
        this.app = app
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getPlayerRepository(mediaPlayer: MediaPlayer): PlayerRepository {
        return PlayerRepositoryImpl(mediaPlayer)
    }

    fun providePlayerInteractor(mediaPlayer: MediaPlayer): PlayerInteractor {
        return PlayerImpl(getPlayerRepository(mediaPlayer))
    }

    private fun createSharedPreferences(): SharedPreferences {
        return app.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
    }

    fun getSharedPreferencesRepository(): SharedPreferencesRepository {
        return SharedPreferencesImpl(createSharedPreferences())
    }

    private fun createGson(): Gson {
        return Gson()
    }

    private fun getDataMapperRepository(): DataMapperRepository {
        return DataMapper(createGson())
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryImpl(getSharedPreferencesRepository(), getDataMapperRepository())
    }
}
