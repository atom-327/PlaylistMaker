package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.data.dto.ITunesRequest
import com.practicum.playlistmaker.data.dto.ITunesResponse
import com.practicum.playlistmaker.domain.api.TracksRepository

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(ITunesRequest(expression))
        if (response.resultCode) {
            return (response as ITunesResponse).results.map {
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
            }
        } else {
            return emptyList()
        }
    }
}
