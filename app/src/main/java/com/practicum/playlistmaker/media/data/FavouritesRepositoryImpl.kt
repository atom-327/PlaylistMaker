package com.practicum.playlistmaker.media.data

import com.practicum.playlistmaker.core.domain.models.Track
import com.practicum.playlistmaker.db.data.AppDatabase
import com.practicum.playlistmaker.db.data.entity.TrackEntity
import com.practicum.playlistmaker.db.data.mapper.TrackDbConvertor
import com.practicum.playlistmaker.media.domain.api.FavouritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavouritesRepositoryImpl(
    private val appDatabase: AppDatabase, private val trackDbConvertor: TrackDbConvertor
) : FavouritesRepository {

    override suspend fun addTrack(track: Track) {
        appDatabase.trackDao().insertTrack(convertToTrackEntity(track))
    }

    override suspend fun deleteTrack(track: Track) {
        appDatabase.trackDao().deleteTrack(convertToTrackEntity(track))
    }

    override fun getTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        val loadedTracks = tracks.map { loadedTrack ->
            loadedTrack.copy(isFavorite = true)
        }
        val sortedTracks = loadedTracks.sortedByDescending { it.trackId }
        emit(convertFromTrackEntity(sortedTracks))
    }

    private fun convertToTrackEntity(track: Track): TrackEntity {
        return trackDbConvertor.map(track)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}
