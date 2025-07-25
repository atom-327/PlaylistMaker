package com.practicum.playlistmaker.player.presentation.view_model

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.core.domain.models.Track
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.presentation.PlayerState
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor

class AudioPlayerViewModel(
    private val player: PlayerInteractor, private val searchHistory: SearchHistoryInteractor
) : ViewModel() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val REFRESH_SECONDS_VALUE_MILLIS = 200L
    }

    private lateinit var track: Track

    init {
        preparePlayer()
    }

    private var mainThreadHandler = android.os.Handler(Looper.getMainLooper())
    private val timerRunnable = Runnable {
        if (state.value?.state == STATE_PLAYING) {
            startTimerUpdate()
        }
    }

    private val state = MutableLiveData(
        PlayerState(
            track,
            state = STATE_DEFAULT,
            timer = player.resetTimer(),
            isPlayButtonEnabled = false,
            isTrackLicked = false
        )
    )

    fun getState(): LiveData<PlayerState> = state

    fun changeLickedButtonStyle() {
        if (state.value?.isTrackLicked == false) {
            state.value = state.value?.copy(isTrackLicked = true)
        } else state.value = state.value?.copy(isTrackLicked = false)
    }

    fun onPause() {
        pausePlayer()
    }

    fun onDestroy() {
        player.release()
        player.resetTimer()
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
        player.resetTimer()
        mainThreadHandler.removeCallbacks(timerRunnable)
    }

    fun playbackControl() {
        when (state.value?.state) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun preparePlayer() {
        track = searchHistory.getListeningTrack()!!
        player.prepare(track.previewUrl)
        player.getMediaPlayer().setOnPreparedListener {
            state.value = state.value?.copy(isPlayButtonEnabled = true)
            renderState(STATE_PREPARED)
        }
        player.getMediaPlayer().setOnCompletionListener {
            renderState(STATE_PREPARED)
            mainThreadHandler.removeCallbacks(timerRunnable)
            state.value = state.value?.copy(timer = player.resetTimer())
        }
    }

    private fun startPlayer() {
        player.play()
        renderState(STATE_PLAYING)
        startTimerUpdate()
    }

    private fun pausePlayer() {
        mainThreadHandler.removeCallbacks(timerRunnable)
        player.pause()
        renderState(STATE_PAUSED)
    }

    private fun startTimerUpdate() {
        state.value = state.value?.copy(timer = player.getCurrentPosition())
        mainThreadHandler.postDelayed(timerRunnable, REFRESH_SECONDS_VALUE_MILLIS)
    }

    private fun renderState(state: Int) {
        this.state.value = this.state.value?.copy(state = state)
    }
}
