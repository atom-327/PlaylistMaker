package com.practicum.playlistmaker.player.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.core.domain.models.Playlist
import com.practicum.playlistmaker.core.domain.models.Track
import com.practicum.playlistmaker.media.domain.api.FavouritesInteractor
import com.practicum.playlistmaker.media.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.media.presentation.PlaylistsState
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.presentation.PlayerState
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val player: PlayerInteractor,
    private val searchHistory: SearchHistoryInteractor,
    private val favouritesInteractor: FavouritesInteractor,
    private val playlistsInteractor: PlaylistsInteractor,
    private val trackAddMessage: String,
    private val trackAddedMessage: String
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
        viewModelScope.launch {
            preparePlayer()
        }
    }

    private val state = MutableLiveData(
        PlayerState(
            track = track,
            state = STATE_DEFAULT,
            timer = player.resetTimer(),
            isPlayButtonEnabled = false,
            isTrackLicked = false,
            addedTrackState = false,
            message = null
        )
    )

    fun getState(): LiveData<PlayerState?> = state

    fun onFavouriteClicked() {
        viewModelScope.launch {
            if (state.value?.isTrackLicked == true) {
                favouritesInteractor.deleteTrack(track)
                changeLickedButtonStyle(false)
            } else {
                favouritesInteractor.addTrack(track)
                changeLickedButtonStyle(true)
            }
        }
    }

    private suspend fun checkIfTrackIsFavorite() {
        val favoriteTracks = player.getIdTracks()
        val isFavorite = favoriteTracks.contains(track.trackId)
        state.value = state.value?.copy(isTrackLicked = isFavorite)
    }

    private fun changeLickedButtonStyle(isFavorite: Boolean) {
        state.value = state.value?.copy(isTrackLicked = isFavorite)
    }

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

    private suspend fun preparePlayer() {
        track = searchHistory.getListeningTrack()!!
        player.prepare(track.previewUrl)
        checkIfTrackIsFavorite()
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

    private val stateLiveData = MutableLiveData<PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = stateLiveData

    fun fillData() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect { playlists ->
                processResult(playlists)
            }
        }
    }

    fun onTrackAddToPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            val result = playlistsInteractor.addTrackToPlaylist(track, playlist)

            val message = if (result) {
                "$trackAddMessage ${playlist.playlistName}"
            } else {
                "$trackAddedMessage ${playlist.playlistName}"
            }

            state.value = state.value?.copy(
                addedTrackState = result,
                message = message
            )
        }
    }

    fun resetMessage() {
        state.value = state.value?.copy(message = null)
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(PlaylistsState.Empty)
        } else {
            renderState(PlaylistsState.Content(playlists))
        }
    }

    private fun renderState(state: PlaylistsState) {
        stateLiveData.postValue(state)
    }
}
