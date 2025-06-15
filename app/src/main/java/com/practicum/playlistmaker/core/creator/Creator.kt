package com.practicum.playlistmaker.core.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.google.gson.Gson
import com.practicum.playlistmaker.player.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.core.data.SharedPreferencesImpl
import com.practicum.playlistmaker.search.data.TracksRepositoryImpl
import com.practicum.playlistmaker.core.data.mapper.DataMapper
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.core.domain.api.DataMapperRepository
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.core.domain.api.SharedPreferencesRepository
import com.practicum.playlistmaker.core.domain.models.Track
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.search.data.network.ITunesAPI
import com.practicum.playlistmaker.search.domain.impl.SearchHistoryImpl
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import com.practicum.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {

    private lateinit var app: Application
    private const val SHARED_PREF = "ThemePrefs"

    fun initApplication(app: Application) {
        Creator.app = app
    }

    private fun getMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }

    private fun createSharedPreferences(): SharedPreferences {
        return app.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
    }

    private fun createGson(): Gson {
        return Gson()
    }

    private fun createItunesService(): ITunesAPI {
        val iTunesBaseUrl = "https://itunes.apple.com"

        val retrofit =
            Retrofit.Builder().baseUrl(iTunesBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit.create(ITunesAPI::class.java)
    }

    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context, createItunesService()))
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(app))
    }

    private fun getPlayerRepository(mediaPlayer: MediaPlayer): PlayerRepository {
        return PlayerRepositoryImpl(mediaPlayer)
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository(getMediaPlayer()))
    }

    fun getListeningTrack(): Track {
        return provideSearchHistoryInteractor().getListeningTrack()!!
    }

    fun getSharedPreferencesRepository(): SharedPreferencesRepository {
        return SharedPreferencesImpl(createSharedPreferences())
    }

    private fun getDataMapperRepository(): DataMapperRepository {
        return DataMapper(createGson())
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryImpl(getSharedPreferencesRepository(), getDataMapperRepository())
    }

    private fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(app))
    }

    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator(context))
    }
}
