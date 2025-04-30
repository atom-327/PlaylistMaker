package com.practicum.playlistmaker.domain.api

interface PlayerRepository {
    fun start()
    fun pause()
    fun prepare(trackUrl: String)
    fun release()
    fun getCurrentPosition(): Long
}
