package com.practicum.playlistmaker.data.mapper

import com.google.gson.Gson
import com.practicum.playlistmaker.domain.api.DataMapperRepository
import com.practicum.playlistmaker.domain.models.Track

class DataMapper(private val gson: Gson) : DataMapperRepository {
    override fun createJsonFromTracks(tracks: Array<Track>): String {
        return gson.toJson(tracks)
    }

    override fun createTracksFromJson(json: String): Array<Track> {
        return gson.fromJson(json, Array<Track>::class.java)
    }
}
