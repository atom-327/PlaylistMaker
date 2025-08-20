package com.practicum.playlistmaker.media.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.core.domain.models.Track
import com.practicum.playlistmaker.media.domain.api.FavouritesInteractor
import com.practicum.playlistmaker.media.presentation.FavouritesState
import kotlinx.coroutines.launch

class FavouritesViewModel(
    private val favouritesInteractor: FavouritesInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavouritesState>()
    fun observeState(): LiveData<FavouritesState> = stateLiveData

    fun fillData() {
        renderState(FavouritesState.Loading)
        viewModelScope.launch {
            favouritesInteractor.getTracks().collect { tracks ->
                processResult(tracks)
            }
        }
    }

    private fun processResult(movies: List<Track>) {
        if (movies.isEmpty()) {
            renderState(FavouritesState.Empty)
        } else {
            renderState(FavouritesState.Content(movies))
        }
    }

    private fun renderState(state: FavouritesState) {
        stateLiveData.postValue(state)
    }
}
