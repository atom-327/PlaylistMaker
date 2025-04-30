package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun loadTracks(storyTracks: MutableList<Track>)
    fun addTrack(storyTracks: MutableList<Track>, track: Track)
    fun clearHistory(storyTracks: MutableList<Track>)
    fun getListeningTrack(): Track?
}
