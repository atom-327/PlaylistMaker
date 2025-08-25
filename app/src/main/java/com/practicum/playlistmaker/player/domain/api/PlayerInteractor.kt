package com.practicum.playlistmaker.player.domain.api

import android.media.MediaPlayer

interface PlayerInteractor {
    fun play()
    fun pause()
    fun stop()
    fun prepare(trackUrl: String)
    fun release()
    fun getCurrentPosition(): String?
    fun resetTimer(): String?
    fun getMediaPlayer(): MediaPlayer
    suspend fun getIdTracks(): List<Int>
}
