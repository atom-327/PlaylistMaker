package com.practicum.playlistmaker.search.presentation

import com.practicum.playlistmaker.core.domain.models.Track

interface TracksState {

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
