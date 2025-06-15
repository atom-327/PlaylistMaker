package com.practicum.playlistmaker.player.presentation.view_model

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.presentation.PlayerState

class AudioPlayerViewModel(private val player: PlayerInteractor, private val url: String) :
    ViewModel() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val REFRESH_SECONDS_VALUE_MILLIS = 200L

        fun factory(player: PlayerInteractor, trackUrl: String): ViewModelProvider.Factory =
            viewModelFactory { initializer { AudioPlayerViewModel(player, trackUrl) } }
    }

    private var mainThreadHandler = android.os.Handler(Looper.getMainLooper())
    private val timerRunnable = Runnable {
        if (state.value?.state == STATE_PLAYING) {
            startTimerUpdate()
        }
    }

    private val state = MutableLiveData(
        PlayerState(
            state = STATE_DEFAULT,
            timer = player.resetTimer(),
            isPlayButtonEnabled = false,
            isTrackLicked = false
        )
    )

    fun getState(): LiveData<PlayerState> = state

    init {
        preparePlayer()
    }

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
        player.prepare(url)
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
