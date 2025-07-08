package com.practicum.playlistmaker.player.presentation

import com.practicum.playlistmaker.core.domain.models.Track

data class PlayerState(
    val track: Track?,
    val state: Int,
    val timer: String?,
    val isPlayButtonEnabled: Boolean,
    val isTrackLicked: Boolean
)
