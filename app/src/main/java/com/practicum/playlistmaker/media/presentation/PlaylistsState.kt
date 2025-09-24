package com.practicum.playlistmaker.media.presentation

import com.practicum.playlistmaker.core.domain.models.Playlist

sealed interface PlaylistsState {

    data object Loading : PlaylistsState

    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistsState

    object Empty : PlaylistsState
}
