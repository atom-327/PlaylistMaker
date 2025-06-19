package com.practicum.playlistmaker.search.presentation.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.core.domain.models.Track
import com.practicum.playlistmaker.search.presentation.TracksState
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistory: SearchHistoryInteractor,
    private val errorStr: String,
    private val emptyStr: String
) : ViewModel() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1_000L
        private const val SEARCH_DEBOUNCE_DELAY = 2_000L
        private const val TEXT_VALUE = ""
    }

    private var isClickAllowed = true
    var changedText: String = TEXT_VALUE

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }

    private val state = MutableLiveData<TracksState>()
    fun getState(): LiveData<TracksState> = state

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    fun searchDebounce(changedText: String) {
        if (this.changedText == changedText) {
            return
        }

        this.changedText = changedText
        onCleared()

        if (changedText.isNotEmpty()) {
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }
    }

    fun search() {
        if (changedText.isNotEmpty()) {
            renderState(TracksState.Loading)

            tracksInteractor.searchTracks(changedText, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
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
            })
        }
    }

    private fun renderState(state: TracksState) {
        this.state.postValue(state)
    }

    fun loadTracks(tracks: MutableList<Track>) {
        searchHistory.loadTracks(tracks)
    }

    fun clearHistory(tracks: MutableList<Track>) {
        searchHistory.clearHistory(tracks)
    }

    fun addTrack(tracks: MutableList<Track>, track: Track) {
        searchHistory.addTrack(tracks, track)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(searchRunnable)
    }
}
