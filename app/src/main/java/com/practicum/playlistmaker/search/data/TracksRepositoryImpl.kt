package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.search.data.dto.ITunesRequest
import com.practicum.playlistmaker.search.data.dto.ITunesResponse
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.util.Resource

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(ITunesRequest(expression))
        return when (response.resultCode) {
            -1 -> Resource.Error(R.string.something_went_wrong.toString())
            200 -> Resource.Success((response as ITunesResponse).results.map {
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
                    it.previewUrl
                )
            })

            else -> {
                return Resource.Error(R.string.nothing_found.toString())
            }
        }
    }
}
