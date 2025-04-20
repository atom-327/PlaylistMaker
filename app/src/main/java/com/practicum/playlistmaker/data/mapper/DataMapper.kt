package com.practicum.playlistmaker.data.mapper

import com.google.gson.Gson
import com.practicum.playlistmaker.domain.models.Track

object DataMapper {
    fun createJsonFromTracks(gson: Gson, tracks: Array<Track>): String {
        return gson.toJson(tracks)
    }

    fun createTracksFromJson(gson: Gson, json: String): Array<Track> {
        return gson.fromJson(json, Array<Track>::class.java)
    }
}
