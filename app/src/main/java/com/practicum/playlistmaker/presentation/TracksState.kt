package com.practicum.playlistmaker.presentation

import com.practicum.playlistmaker.domain.models.Track

sealed interface TracksState {

    data object Loading : TracksState

    data class Content(
        val tracks: List<Track>
    ) : TracksState

    data class Empty(
        val emptyMessage: String
    ) : TracksState

    data class Error(
        val errorMessage: String
    ) : TracksState
}
