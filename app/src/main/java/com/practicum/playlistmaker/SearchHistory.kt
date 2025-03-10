package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.SearchActivity.Companion.TRACK_ID

class SearchHistory(private val sharedPreferences: SharedPreferences) : Application() {

    fun loadTracks(storyTracks: ArrayList<Track>) {
        val track = sharedPreferences.getString(TRACK_ID, null)
        if (track != null) {
            storyTracks.clear()
            storyTracks.addAll(createTracksFromJson(track))
        }
    }

    fun addTrack(storyTracks: ArrayList<Track>, track: Track) {
        val existingTrack = storyTracks.find { it.trackId == track.trackId }
        if (existingTrack != null) {
            storyTracks.remove(existingTrack)
        }
        storyTracks.add(0, track)
        if (storyTracks.size > 10) {
            storyTracks.removeAt(storyTracks.size - 1)
        }
        val array = storyTracks.toTypedArray()
        sharedPreferences.edit().remove(TRACK_ID).putString(TRACK_ID, createJsonFromTracks(array))
            .apply()
    }

    fun clearHistory(storyTracks: ArrayList<Track>) {
        sharedPreferences.edit().remove(TRACK_ID).apply()
        storyTracks.clear()
    }

    private fun createJsonFromTracks(tracks: Array<Track>): String {
        return Gson().toJson(tracks)
    }

    private fun createTracksFromJson(json: String): Array<Track> {
        return Gson().fromJson(json, Array<Track>::class.java)
    }
}
