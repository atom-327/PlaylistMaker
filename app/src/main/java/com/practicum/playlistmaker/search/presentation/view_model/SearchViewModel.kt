package com.practicum.playlistmaker.search.presentation.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.creator.Creator
import com.practicum.playlistmaker.core.domain.models.Track
import com.practicum.playlistmaker.core.presentation.TracksState
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.core.ui.App

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1_000L
        private const val SEARCH_DEBOUNCE_DELAY = 2_000L
        private const val TEXT_VALUE = ""

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(this[APPLICATION_KEY] as App)
            }
        }
    }

    private var isClickAllowed = true
    var changedText: String = TEXT_VALUE

    private val tracksInteractor = Creator.provideTracksInteractor(getApplication())

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
                                    getApplication<App>().getString(R.string.something_went_wrong),
                                )
                            )
                        }

                        tracks.isEmpty() -> {
                            renderState(
                                TracksState.Empty(
                                    getApplication<App>().getString(R.string.nothing_found),
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

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(searchRunnable)
    }
}
