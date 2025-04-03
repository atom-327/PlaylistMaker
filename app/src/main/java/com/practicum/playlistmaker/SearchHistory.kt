package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.SearchActivity.Companion.TRACK_ID

class SearchHistory(private val sharedPreferences: SharedPreferences) : Application() {

    companion object {
        private const val STORY_SIZE = 10
    }

    private val gson = Gson()

    fun loadTracks(storyTracks: MutableList<Track>) {
        val track = sharedPreferences.getString(TRACK_ID, null)
        if (track != null) {
            storyTracks.clear()
            storyTracks.addAll(createTracksFromJson(track))
        }
    }

    fun addTrack(storyTracks: MutableList<Track>, track: Track) {
        val existingTrack = storyTracks.find { it.trackId == track.trackId }
        if (existingTrack != null) {
            storyTracks.remove(existingTrack)
        }
        storyTracks.add(0, track)
        if (storyTracks.size > STORY_SIZE) {
            storyTracks.removeAt(storyTracks.size - 1)
        }
        val array = storyTracks.toTypedArray()
        sharedPreferences.edit().remove(TRACK_ID).putString(TRACK_ID, createJsonFromTracks(array))
            .apply()
    }

    fun clearHistory(storyTracks: MutableList<Track>) {
        sharedPreferences.edit().remove(TRACK_ID).apply()
        storyTracks.clear()
    }

    fun getListeningTrack(): Track? {
        val trackStr = sharedPreferences.getString(TRACK_ID, null)
        if (trackStr != null) {
            val tracks = createTracksFromJson(trackStr)
            return tracks[0]
        } else return null
    }

    private fun createJsonFromTracks(tracks: Array<Track>): String {
        return gson.toJson(tracks)
    }

    private fun createTracksFromJson(json: String): Array<Track> {
        return gson.fromJson(json, Array<Track>::class.java)
    }
}
