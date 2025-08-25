package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.db.data.dao.TrackDao
import com.practicum.playlistmaker.player.domain.api.PlayerRepository

class PlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer,
    private val trackDao: TrackDao
) : PlayerRepository {

    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun stop() {
        mediaPlayer.stop()
    }

    override fun prepare(trackUrl: String) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Long = mediaPlayer.currentPosition.toLong()

    override fun getMediaPlayer(): MediaPlayer = mediaPlayer

    override suspend fun getIdTracks(): List<Int> {
        return trackDao.getIdTracks()
    }
}
