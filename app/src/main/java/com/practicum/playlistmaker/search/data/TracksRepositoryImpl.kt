package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.domain.models.Track
import com.practicum.playlistmaker.search.data.dto.ITunesRequest
import com.practicum.playlistmaker.search.data.dto.ITunesResponse
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.core.util.Resource
import com.practicum.playlistmaker.db.data.AppDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase,
) : TracksRepository {
    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(ITunesRequest(expression))
        when (response.resultCode) {
            -1 -> emit(Resource.Error(R.string.something_went_wrong.toString()))
            200 -> {
                val favouriteTrackIds = appDatabase.trackDao().getIdTracks()
                val tracks = (response as ITunesResponse).results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.getTrackTime(),
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl,
                        isFavorite = favouriteTrackIds.contains(it.trackId)
                    )
                }
                emit(Resource.Success(tracks))
            }

            else -> {
                emit(Resource.Success(emptyList()))
            }
        }
    }
}
