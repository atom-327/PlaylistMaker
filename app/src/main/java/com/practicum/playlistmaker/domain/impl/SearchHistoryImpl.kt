package com.practicum.playlistmaker.domain.impl

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.App.Companion.TRACK_ID
import com.practicum.playlistmaker.data.mapper.DataMapper
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.models.Track

class SearchHistoryImpl(private val sharedPreferences: SharedPreferences) :
    SearchHistoryInteractor {

    companion object {
        private const val STORY_SIZE = 10
    }

    private val gson = Gson()

    override fun loadTracks(storyTracks: MutableList<Track>) {
        val track = sharedPreferences.getString(TRACK_ID, null)
        if (track != null) {
            storyTracks.clear()
            storyTracks.addAll(DataMapper.createTracksFromJson(gson, track))
        }
    }

    override fun addTrack(storyTracks: MutableList<Track>, track: Track) {
        val existingTrack = storyTracks.find { it.trackId == track.trackId }
        if (existingTrack != null) {
            storyTracks.remove(existingTrack)
        }
        storyTracks.add(0, track)
        if (storyTracks.size > STORY_SIZE) {
            storyTracks.removeAt(storyTracks.size - 1)
        }
        val array = storyTracks.toTypedArray()
        sharedPreferences.edit().remove(TRACK_ID)
            .putString(TRACK_ID, DataMapper.createJsonFromTracks(gson, array)).apply()
    }

    override fun clearHistory(storyTracks: MutableList<Track>) {
        sharedPreferences.edit().remove(TRACK_ID).apply()
        storyTracks.clear()
    }

    override fun getListeningTrack(): Track? {
        val trackStr = sharedPreferences.getString(TRACK_ID, null)
        if (trackStr != null) {
            val tracks = DataMapper.createTracksFromJson(gson, trackStr)
            return tracks[0]
        } else return null
    }
}
