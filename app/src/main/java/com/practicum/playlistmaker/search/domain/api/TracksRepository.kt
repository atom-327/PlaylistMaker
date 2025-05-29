package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.util.Resource

interface TracksRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
}
