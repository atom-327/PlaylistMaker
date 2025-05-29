package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun loadTracks(): List<Track>
    fun addTrack(storyTracks: MutableList<Track>, track: Track)
    fun clearHistory(storyTracks: MutableList<Track>)
    fun getListeningTrack(): Track?
}
