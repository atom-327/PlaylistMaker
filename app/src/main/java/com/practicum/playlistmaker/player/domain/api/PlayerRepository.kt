package com.practicum.playlistmaker.player.domain.api

import android.media.MediaPlayer

interface PlayerRepository {
    fun start()
    fun pause()
    fun prepare(trackUrl: String)
    fun release()
    fun getCurrentPosition(): Long
    fun getMediaPlayer(): MediaPlayer
}
