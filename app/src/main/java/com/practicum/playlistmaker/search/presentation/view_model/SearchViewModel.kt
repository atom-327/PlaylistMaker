package com.practicum.playlistmaker.search.presentation.view_model

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.core.domain.api.SharedPreferencesRepository
import com.practicum.playlistmaker.core.domain.models.Track
import com.practicum.playlistmaker.core.util.debounce
import com.practicum.playlistmaker.search.presentation.TracksState
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistory: SearchHistoryInteractor,
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    private val errorStr: String,
    private val emptyStr: String
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2_000L
        private const val TEXT_VALUE = ""
    }

    var changedText: String = TEXT_VALUE

    private val state = MutableLiveData<TracksState>()
    fun getState(): LiveData<TracksState> = state

    private val trackSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) {
            search()
        }

    fun searchDebounce(changedText: String) {
        if (this.changedText != changedText) {
            this.changedText = changedText
            trackSearchDebounce(changedText)
        }
    }

    fun search() {
        if (changedText.isNotEmpty()) {
            renderState(TracksState.Loading)

            viewModelScope.launch {
                tracksInteractor.searchTracks(changedText).collect { pair ->
                    processResult(pair.first, pair.second)
                }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }

        when {
            errorMessage != null -> {
                renderState(
                    TracksState.Error(
                        errorStr
                    )
                )
            }

            tracks.isEmpty() -> {
                renderState(
                    TracksState.Empty(
                        emptyStr
                    )
                )
            }

            else -> renderState(TracksState.Content(tracks))
        }
    }

    fun clearSearch() {
        changedText = ""
        renderState(TracksState.Content(emptyList()))
    }

    private fun renderState(state: TracksState) {
        this.state.postValue(state)
    }

    fun loadTracks(tracks: MutableList<Track>) {
        viewModelScope.launch {
            searchHistory.loadTracks(tracks)
        }
    }

    fun clearHistory(tracks: MutableList<Track>) {
        searchHistory.clearHistory(tracks)
    }

    fun addTrack(tracks: MutableList<Track>, track: Track) {
        searchHistory.addTrack(tracks, track)
    }

    fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferencesRepository.registerOnSharedPreferenceChangeListener(listener)
    }
}
