package com.practicum.playlistmaker.player.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.core.domain.models.Track
import com.practicum.playlistmaker.media.domain.api.FavouritesInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.presentation.PlayerState
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val player: PlayerInteractor,
    private val searchHistory: SearchHistoryInteractor,
    private val favouritesInteractor: FavouritesInteractor
) : ViewModel() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val REFRESH_SECONDS_VALUE_MILLIS = 300L
    }

    private var timerJob: Job? = null
    private lateinit var track: Track

    init {
        preparePlayer()
    }

    fun onFavouriteClicked() {
        var isFavorite = true
        viewModelScope.launch {
            if (track.isFavorite) {
                favouritesInteractor.deleteTrack(track)
            } else {
                favouritesInteractor.addTrack(track)
                isFavorite = false
            }
        }
        changeLickedButtonStyle(isFavorite)
    }

    private fun changeLickedButtonStyle(isFavorite: Boolean) {
        if (state.value?.isTrackLicked == false) {
            state.value = state.value?.copy(isTrackLicked = true)
        } else state.value = state.value?.copy(isTrackLicked = false)
    }

    private val state = MutableLiveData(
        PlayerState(
            track = track,
            state = STATE_DEFAULT,
            timer = player.resetTimer(),
            isPlayButtonEnabled = false,
            isTrackLicked = false
        )
    )

    fun getState(): LiveData<PlayerState> = state

    fun onPause() {
        pausePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
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
            timerJob?.cancel()
            renderState(STATE_PREPARED)
            state.value = state.value?.copy(timer = player.resetTimer())
        }
    }

    private fun startPlayer() {
        player.play()
        renderState(STATE_PLAYING)
        startTimerUpdate()
    }

    private fun pausePlayer() {
        player.pause()
        timerJob?.cancel()
        renderState(STATE_PAUSED)
    }

    private fun releasePlayer() {
        player.stop()
        player.release()
        state.value = state.value?.copy(timer = player.resetTimer())
        renderState(STATE_DEFAULT)
    }

    private fun startTimerUpdate() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (state.value?.state == STATE_PLAYING) {
                delay(REFRESH_SECONDS_VALUE_MILLIS)
                state.value = state.value?.copy(timer = player.getCurrentPosition())
            }
        }
    }

    private fun renderState(state: Int) {
        this.state.value = this.state.value?.copy(state = state)
    }
}
