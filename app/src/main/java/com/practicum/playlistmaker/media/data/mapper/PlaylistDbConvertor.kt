package com.practicum.playlistmaker.media.data.mapper

import com.practicum.playlistmaker.core.domain.models.Playlist
import com.practicum.playlistmaker.media.data.entity.PlaylistEntity

class PlaylistDbConvertor {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.pathToPlaylistIcon,
            playlist.tracks,
            playlist.numberOfTracks
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.pathToPlaylistIcon,
            playlist.tracks,
            playlist.numberOfTracks
        )
    }
}
