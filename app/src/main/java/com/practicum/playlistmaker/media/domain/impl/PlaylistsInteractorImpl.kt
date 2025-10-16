package com.practicum.playlistmaker.media.domain.impl

import android.net.Uri
import com.practicum.playlistmaker.core.domain.models.Playlist
import com.practicum.playlistmaker.core.domain.models.Track
import com.practicum.playlistmaker.media.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.media.domain.api.PlaylistsRepository
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val playlistsRepository: PlaylistsRepository) :
    PlaylistsInteractor {

    override suspend fun addPlaylist(playlist: Playlist, imageUri: Uri?) {
        playlistsRepository.addPlaylist(playlist, imageUri)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        playlistsRepository.deletePlaylist(playlistId)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getPlaylists()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean {
        return playlistsRepository.addTrackToPlaylist(track, playlist)
    }

    override suspend fun getPlaylistById(playlistId: Int): Playlist? {
        return playlistsRepository.getPlaylistById(playlistId)
    }

    override fun getTracksByPlaylistId(playlistId: Int): Flow<List<Track>> {
        return playlistsRepository.getTracksByPlaylistId(playlistId)
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Int, playlistId: Int) {
        playlistsRepository.deleteTrackFromPlaylist(trackId, playlistId)
    }

    override fun sharePlaylist(message: String) {
        playlistsRepository.sharePlaylist(message)
    }

    override suspend fun updatePlaylist(playlist: Playlist, imageUri: Uri?) {
        playlistsRepository.updatePlaylist(playlist, imageUri)
    }
}
