package com.practicum.playlistmaker.player.presentation.view_model

import android.app.Application
import android.media.MediaPlayer
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.App

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

    private var mediaPlayer: MediaPlayer = MediaPlayer()
    private var mainThreadHandler = android.os.Handler(Looper.getMainLooper())
    private val timerRunnable: Runnable = Runnable { refreshTrackTimer() }

    private val player = Creator.providePlayerInteractor(mediaPlayer)
    private val searchHistory = Creator.provideSearchHistoryInteractor()

    private val state = MutableLiveData(STATE_DEFAULT)
    fun getState(): LiveData<Int> = state

    fun getListeningTrack(): Track {
        return searchHistory.getListeningTrack()!!
    }

    fun getCurrentPosition(): String {
        return player.getCurrentPosition()!!
    }

    fun resetTimer(): String {
        return player.resetTimer()!!
    }

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
        mediaPlayer.setOnPreparedListener {
            renderState(STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
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
        renderState(STATE_PAUSED)
        mainThreadHandler?.removeCallbacks(timerRunnable)
    }

    private fun refreshTrackTimer() {
        if (state.value == STATE_PLAYING) {
            mainThreadHandler?.postDelayed(timerRunnable, REFRESH_SECONDS_VALUE_MILLIS)
        }
    }

    private fun renderState(state: Int) {
        this.state.postValue(state)
    }
}
