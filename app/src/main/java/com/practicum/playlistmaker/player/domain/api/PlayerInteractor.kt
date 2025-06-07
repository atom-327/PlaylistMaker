package com.practicum.playlistmaker.player.domain.api

import android.media.MediaPlayer

interface PlayerInteractor {
    fun play()
    fun pause()
    fun prepare(trackUrl: String)
    fun release()
    fun getCurrentPosition(): String?
    fun resetTimer(): String?
    fun getMediaPlayer(): MediaPlayer
}
