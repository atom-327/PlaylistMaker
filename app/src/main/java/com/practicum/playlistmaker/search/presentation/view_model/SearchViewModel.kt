package com.practicum.playlistmaker.search.presentation.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.creator.Creator
import com.practicum.playlistmaker.core.domain.models.Track
import com.practicum.playlistmaker.search.presentation.TracksState
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor

class SearchViewModel(
    private val tracksHistory: SearchHistoryInteractor,
    private val errorStr: String,
    private val emptyStr: String
) : ViewModel() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1_000L
        private const val SEARCH_DEBOUNCE_DELAY = 2_000L
        private const val TEXT_VALUE = ""

        fun factory(searchHistory: SearchHistoryInteractor): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val app = (this[APPLICATION_KEY] as Application)
                    val errorMessage = app.getString(R.string.something_went_wrong)
                    val emptyMessage = app.getString(R.string.nothing_found)
                    SearchViewModel(searchHistory, errorMessage, emptyMessage)
                }
            }
    }

    private var isClickAllowed = true
    var changedText: String = TEXT_VALUE

    private val tracksInteractor = Creator.provideTracksInteractor()

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
        tracksHistory.loadTracks(tracks)
    }

    fun clearHistory(tracks: MutableList<Track>) {
        tracksHistory.clearHistory(tracks)
    }

    fun addTrack(tracks: MutableList<Track>, track: Track) {
        tracksHistory.addTrack(tracks, track)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(searchRunnable)
    }
}
