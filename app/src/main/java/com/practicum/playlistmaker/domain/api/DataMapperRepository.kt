package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface DataMapperRepository {
    fun createJsonFromTracks(tracks: Array<Track>): String
    fun createTracksFromJson(json: String): Array<Track>
}
