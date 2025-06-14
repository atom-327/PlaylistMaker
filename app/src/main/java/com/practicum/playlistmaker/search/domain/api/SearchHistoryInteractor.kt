package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.core.domain.models.Track

interface SearchHistoryInteractor {
    fun loadTracks(storyTracks: MutableList<Track>)
    fun addTrack(storyTracks: MutableList<Track>, track: Track)
    fun clearHistory(storyTracks: MutableList<Track>)
    fun getListeningTrack(): Track?
}
