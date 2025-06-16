package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.core.domain.models.Track
import com.practicum.playlistmaker.core.util.Resource

interface TracksRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
}
