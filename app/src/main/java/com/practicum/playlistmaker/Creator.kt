package com.practicum.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.data.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {

    private lateinit var app: Application
    private const val SHARED_PREF = "ThemePrefs"

    fun initApplication(app: Application) {
        this.app = app
    }

    private fun getMoviesRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideMoviesInteractor(): TracksInteractor {
        return TracksInteractorImpl(getMoviesRepository())
    }

    fun createSharedPreferences(): SharedPreferences {
        return app.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
    }
}
