package com.practicum.playlistmaker.core.ui

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.markodevcic.peko.PermissionRequester
import com.practicum.playlistmaker.core.domain.api.SharedPreferencesRepository
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.interactorModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.viewModelModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

    companion object {
        private const val IS_FIRST_RUN = "is_first_run"
        private const val TEXT_KEY = "isDarkTheme"
    }

    private val sharedPreferencesRepository: SharedPreferencesRepository by inject()
    private var darkTheme = false
    private val context = this

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }

        PermissionRequester.initialize(context)

        darkTheme = getAppTheme()
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPreferencesRepository.putBooleanItem(TEXT_KEY, darkTheme)
    }

    fun getAppTheme(): Boolean {
        if (sharedPreferencesRepository.getBooleanItem(IS_FIRST_RUN)) {
            sharedPreferencesRepository.putBooleanItem(IS_FIRST_RUN, false)
            return when (AppCompatDelegate.getDefaultNightMode()) {
                AppCompatDelegate.MODE_NIGHT_YES -> true
                AppCompatDelegate.MODE_NIGHT_NO -> false
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> isSystemInDarkTheme(context)
                else -> isSystemInDarkTheme(context)
            }
        }
        return sharedPreferencesRepository.getBooleanItem(TEXT_KEY)
    }

    private fun isSystemInDarkTheme(context: Context): Boolean {
        val currentNightMode =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }
}
