package com.practicum.playlistmaker.domain.api

interface PlayerInteractor {
    fun play()
    fun pause()
    fun prepare(trackUrl: String)
    fun release()
    fun getCurrentPosition(): String?
    fun resetTimer(): String?
}
