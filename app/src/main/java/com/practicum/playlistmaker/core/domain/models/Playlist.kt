package com.practicum.playlistmaker.core.domain.models

data class Playlist(
    val playlistId: Int,
    var playlistName: String?,
    var playlistDescription: String?,
    var pathToPlaylistIcon: String?,
    val tracks: String?,
    val numberOfTracks: Int
)
