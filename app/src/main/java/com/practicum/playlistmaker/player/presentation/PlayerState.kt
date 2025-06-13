package com.practicum.playlistmaker.player.presentation

data class PlayerState(
    val state: Int, val timer: String?, val isPlayButtonEnabled: Boolean, val isTrackLicked: Boolean
)
