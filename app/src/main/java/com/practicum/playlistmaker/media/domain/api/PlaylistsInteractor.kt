package com.practicum.playlistmaker.media.domain.api

import android.net.Uri
import com.practicum.playlistmaker.core.domain.models.Playlist
import com.practicum.playlistmaker.core.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun addPlaylist(playlist: Playlist, imageUri: Uri?)
    suspend fun deletePlaylist(playlistId: Int)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean
    suspend fun getPlaylistById(playlistId: Int): Playlist?
    fun getTracksByPlaylistId(playlistId: Int): Flow<List<Track>>
    suspend fun deleteTrackFromPlaylist(trackId: Int, playlistId: Int)
    fun sharePlaylist(message: String)
    suspend fun updatePlaylist(playlist: Playlist, imageUri: Uri?)
}
