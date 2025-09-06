package com.practicum.playlistmaker.media.data

import com.practicum.playlistmaker.core.domain.models.Playlist
import com.practicum.playlistmaker.core.domain.models.Track
import com.practicum.playlistmaker.media.data.dao.AddedTrackDao
import com.practicum.playlistmaker.media.data.dao.PlaylistDao
import com.practicum.playlistmaker.media.data.entity.AddedTrackEntity
import com.practicum.playlistmaker.media.data.entity.PlaylistEntity
import com.practicum.playlistmaker.media.data.mapper.AddedTrackDbConvertor
import com.practicum.playlistmaker.media.data.mapper.PlaylistDbConvertor
import com.practicum.playlistmaker.media.domain.api.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val playlistDAO: PlaylistDao,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val addedTrackDAO: AddedTrackDao,
    private val addedTrackDbConvertor: AddedTrackDbConvertor
) : PlaylistsRepository {

    override suspend fun addPlaylist(playlist: Playlist) {
        val playlistEntity = convertToPlaylistEntity(playlist).copy(
            addedDate = System.currentTimeMillis()
        )
        playlistDAO.insertPlaylist(playlistEntity)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDAO.deletePlaylist(convertToPlaylistEntity(playlist))
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val sortedPlaylists = convertFromPlaylistEntity(
            playlistDAO.getPlaylists().sortedByDescending { it.addedDate })
        emit(sortedPlaylists)
    }

    private fun convertToPlaylistEntity(playlist: Playlist): PlaylistEntity {
        return playlistDbConvertor.map(playlist)
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean {

        val currentTracks = playlist.tracks?.split(",")?.toMutableSet() ?: mutableSetOf()

        if (currentTracks.contains(track.trackId.toString())) {
            return false
        } else {
            val addedTrackEntity = convertToTrackEntity(track).copy(
                addedDate = System.currentTimeMillis()
            )
            addedTrackDAO.insertTrack(addedTrackEntity)

            currentTracks.add(track.trackId.toString())
            val updatedTracks = currentTracks.joinToString(",")

            val updatedPlaylist = playlist.copy(
                tracks = updatedTracks, numberOfTracks = currentTracks.size
            )

            playlistDAO.updatePlaylist(convertToPlaylistEntity(updatedPlaylist))
            return true
        }
    }

    override suspend fun deleteTrack(track: Track) {
        addedTrackDAO.deleteTrack(convertToTrackEntity(track))
    }

    override suspend fun getPlaylistById(playlistId: Int): Playlist? {
        return playlistDAO.getPlaylistById(playlistId)?.let { playlistDbConvertor.map(it) }
    }

    private fun convertToTrackEntity(track: Track): AddedTrackEntity {
        return addedTrackDbConvertor.map(track)
    }
}
