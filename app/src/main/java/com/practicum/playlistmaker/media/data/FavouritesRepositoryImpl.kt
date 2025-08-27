package com.practicum.playlistmaker.media.data

import com.practicum.playlistmaker.core.domain.models.Track
import com.practicum.playlistmaker.db.data.dao.TrackDao
import com.practicum.playlistmaker.db.data.entity.TrackEntity
import com.practicum.playlistmaker.db.data.mapper.TrackDbConvertor
import com.practicum.playlistmaker.media.domain.api.FavouritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavouritesRepositoryImpl(
    private val trackDao: TrackDao, private val trackDbConvertor: TrackDbConvertor
) : FavouritesRepository {

    override suspend fun addTrack(track: Track) {
        val trackEntity = convertToTrackEntity(track).copy(
            addedDate = System.currentTimeMillis()
        )
        trackDao.insertTrack(trackEntity)
    }

    override suspend fun deleteTrack(track: Track) {
        trackDao.deleteTrack(convertToTrackEntity(track))
    }

    override fun getTracks(): Flow<List<Track>> = flow {
        val sortedTracks =
            convertFromTrackEntity(trackDao.getTracks().sortedByDescending { it.addedDate })
        emit(sortedTracks)
    }

    private fun convertToTrackEntity(track: Track): TrackEntity {
        return trackDbConvertor.map(track)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}
