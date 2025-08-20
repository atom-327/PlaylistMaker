package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.core.domain.api.DataMapper
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.core.domain.api.SharedPreferencesRepository
import com.practicum.playlistmaker.core.domain.models.Track
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import kotlinx.coroutines.flow.Flow

class SearchHistoryImpl(
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    private val dataMapper: DataMapper,
    private val tracksRepository: TracksRepository
) : SearchHistoryInteractor {

    companion object {
        private const val TRACK_ID = "TRACK_ID"
        private const val STORY_SIZE = 10
    }

    override fun loadTracks(storyTracks: MutableList<Track>): Flow<List<Track>> {
        val track = sharedPreferencesRepository.getStrItem(TRACK_ID)
        return if (track != null) {
            tracksRepository.loadHistoryTracks(track)
        } else emptyList<Track>() as Flow<List<Track>>
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
        sharedPreferencesRepository.removeItem(TRACK_ID)
        sharedPreferencesRepository.putStrItem(
            TRACK_ID, dataMapper.createJsonFromTracks(storyTracks.toTypedArray())
        )
    }

    override fun clearHistory(storyTracks: MutableList<Track>) {
        sharedPreferencesRepository.removeItem(TRACK_ID)
        storyTracks.clear()
    }

    override fun getListeningTrack(): Track? {
        val trackStr = sharedPreferencesRepository.getStrItem(TRACK_ID)
        if (trackStr != null) {
            val tracks = dataMapper.createTracksFromJson(trackStr)
            return tracks[0]
        } else return null
    }
}
