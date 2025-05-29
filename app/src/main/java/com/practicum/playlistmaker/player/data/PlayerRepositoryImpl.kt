package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.api.PlayerRepository

class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : PlayerRepository {
    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun prepare(trackUrl: String) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Long = mediaPlayer.currentPosition.toLong()
}
