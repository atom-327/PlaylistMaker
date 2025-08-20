package com.practicum.playlistmaker.search.data.dto

import java.text.SimpleDateFormat
import java.util.Locale

class TrackDto(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    private val trackTimeMillis: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) {
    fun getTrackTime(): String {
        val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
        return dateFormat.format(trackTimeMillis.toLong())
    }
}
