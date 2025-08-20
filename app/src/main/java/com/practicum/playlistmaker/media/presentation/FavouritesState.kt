package com.practicum.playlistmaker.media.presentation

import com.practicum.playlistmaker.core.domain.models.Track

sealed interface FavouritesState {

    object Loading : FavouritesState

    data class Content(
        val tracks: List<Track>
    ) : FavouritesState

    object Empty : FavouritesState
}
