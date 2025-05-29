package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerImpl(
    private val playerRepository: PlayerRepository,
) : PlayerInteractor {

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun play() {
        playerRepository.start()
    }

    override fun pause() {
        playerRepository.pause()
    }

    override fun prepare(trackUrl: String) {
        playerRepository.prepare(trackUrl)
    }

    override fun release() {
        playerRepository.release()
    }

    override fun getCurrentPosition(): String? {
        return dateFormat.format(playerRepository.getCurrentPosition())
    }

    override fun resetTimer(): String? {
        return dateFormat.format(0L)
    }
}
