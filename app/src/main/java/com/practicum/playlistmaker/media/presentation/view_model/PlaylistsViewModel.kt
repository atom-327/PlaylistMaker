package com.practicum.playlistmaker.media.presentation.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.core.domain.models.Playlist
import com.practicum.playlistmaker.core.domain.models.Track
import com.practicum.playlistmaker.media.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.media.presentation.PlaylistsState
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.presentation.TracksState
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    val playlistsInteractor: PlaylistsInteractor,
    private val searchHistory: SearchHistoryInteractor,
) : ViewModel() {

    private val _playlistStateLiveData = MutableLiveData<PlaylistsState>()
    fun playlistStateLiveData(): LiveData<PlaylistsState> = _playlistStateLiveData

    private val _tracksStateLiveData = MutableLiveData<TracksState>()
    fun tracksStateLiveData(): LiveData<TracksState> = _tracksStateLiveData

    private val _playlistLength = MutableLiveData<Int>()
    val playlistLength: LiveData<Int> = _playlistLength

    private val _playlistTracksCount = MutableLiveData<Int>()
    val playlistTracksCount: LiveData<Int> = _playlistTracksCount

    private var currentTracks: List<Track> = emptyList()

    fun fillData() {
        renderState(PlaylistsState.Loading)
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect { playlists ->
                processResult(playlists)
            }
        }
    }

    fun onPlaylistCreate(playlist: Playlist, imageUri: Uri?) {
        viewModelScope.launch {
            playlistsInteractor.addPlaylist(playlist, imageUri)
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(PlaylistsState.Empty)
        } else {
            renderState(PlaylistsState.Content(playlists))
        }
    }

    fun loadPlaylistById(playlistId: Int) {
        viewModelScope.launch {
            val playlist = playlistsInteractor.getPlaylistById(playlistId)
            if (playlist != null) {
                renderState(PlaylistsState.PlaylistContent(playlist))
                _playlistTracksCount.value = playlist.numberOfTracks
                loadPlaylistTracks(playlistId)
                playlistsInteractor.getTracksByPlaylistId(playlistId).collect { tracks ->
                    renderState(TracksState.Content(tracks))
                }
            }
        }
    }

    fun updatePlaylist(playlist: Playlist, imageUri: Uri?) {
        viewModelScope.launch {
            playlistsInteractor.updatePlaylist(playlist, imageUri)
        }
    }

    fun deletePlaylist(playlistId: Int) {
        viewModelScope.launch {
            playlistsInteractor.deletePlaylist(playlistId)
        }
    }

    fun deleteTrackFromPlaylist(trackId: Int, playlistId: Int) {
        viewModelScope.launch {
            playlistsInteractor.deleteTrackFromPlaylist(trackId, playlistId)
            updateUIAfterTrackDeletion(trackId, playlistId)
        }
    }

    private fun updateUIAfterTrackDeletion(trackId: Int, playlistId: Int) {
        val updatedTracks = currentTracks.filter { it.trackId != trackId }
        currentTracks = updatedTracks
        renderState(TracksState.Content(updatedTracks))
        _playlistTracksCount.value = updatedTracks.size
        calculateTotalDuration(updatedTracks)
        viewModelScope.launch {
            playlistsInteractor.getTracksByPlaylistId(playlistId).collect { freshTracks ->
                if (freshTracks.size != updatedTracks.size) {
                    currentTracks = freshTracks
                    renderState(TracksState.Content(currentTracks))
                    calculateTotalDuration(currentTracks)
                }
            }
        }
    }

    private fun loadPlaylistTracks(playlistId: Int) {
        viewModelScope.launch {
            playlistsInteractor.getTracksByPlaylistId(playlistId).collect { tracks ->
                currentTracks = tracks
                renderState(TracksState.Content(tracks))
                calculateTotalDuration(tracks)
            }
        }
    }

    private fun calculateTotalDuration(tracks: List<Track>) {
        val totalMillis = tracks.sumOf { track ->
            parseTimeToMillis(track.trackTimeMillis)
        }
        val totalMinutes = (totalMillis / 1000 / 60).toInt()
        _playlistLength.value = totalMinutes
    }

    private fun parseTimeToMillis(timeString: String): Long {
        val parts = timeString.split(":")
        return when (parts.size) {
            2 -> {
                val minutes = parts[0].toLong()
                val seconds = parts[1].toLong()
                (minutes * 60 + seconds) * 1000
            }

            3 -> {
                val hours = parts[0].toLong()
                val minutes = parts[1].toLong()
                val seconds = parts[2].toLong()
                (hours * 3600 + minutes * 60 + seconds) * 1000
            }

            else -> 0L
        }
    }

    fun addTrack(track: Track) {
        searchHistory.addTrack(track)
    }

    fun sharePlaylist(message: String) {
        playlistsInteractor.sharePlaylist(message)
    }

    private fun renderState(state: PlaylistsState) {
        _playlistStateLiveData.postValue(state)
    }

    private fun renderState(state: TracksState) {
        _tracksStateLiveData.postValue(state)
    }
}
