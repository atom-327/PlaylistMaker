package com.practicum.playlistmaker.player.presentation.view_model

import android.app.Application
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.core.creator.Creator
import com.practicum.playlistmaker.core.ui.App

class AudioPlayerViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val REFRESH_SECONDS_VALUE_MILLIS = 300L

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AudioPlayerViewModel(this[APPLICATION_KEY] as App)
            }
        }
    }

    private var mainThreadHandler = android.os.Handler(Looper.getMainLooper())
    private val timerRunnable: Runnable = Runnable { refreshTrackTimer() }

    private val player = Creator.providePlayerInteractor()

    private val state = MutableLiveData(STATE_DEFAULT)
    fun getState(): LiveData<Int> = state

    private val position = MutableLiveData<String>()
    fun getPosition(): LiveData<String> = position

    fun playbackControl() {
        when (state.value) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    fun preparePlayer(trackUrl: String) {
        player.prepare(trackUrl)
        player.getMediaPlayer().setOnPreparedListener {
            renderState(STATE_PREPARED)
        }
        player.getMediaPlayer().setOnCompletionListener {
            renderState(STATE_PREPARED)
            mainThreadHandler?.removeCallbacks(timerRunnable)
        }
    }

    fun onPause() {
        pausePlayer()
    }

    fun onDestroy() {
        player.release()
        mainThreadHandler?.removeCallbacks(timerRunnable)
    }

    private fun startPlayer() {
        player.play()
        renderState(STATE_PLAYING)
        mainThreadHandler?.post(timerRunnable)
    }

    private fun pausePlayer() {
        player.pause()
        mainThreadHandler?.removeCallbacks(timerRunnable)
        renderState(STATE_PAUSED)
    }

    private fun refreshTrackTimer() {
        if (state.value == STATE_PLAYING) {
            position.postValue(player.getCurrentPosition())
            mainThreadHandler?.postDelayed(timerRunnable, REFRESH_SECONDS_VALUE_MILLIS)
        }
    }

    private fun renderState(state: Int) {
        this.state.postValue(state)
    }
}
