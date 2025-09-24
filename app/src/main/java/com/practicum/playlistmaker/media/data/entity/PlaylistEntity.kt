package com.practicum.playlistmaker.media.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val playlistId: Int,
    val playlistName: String?,
    val playlistDescription: String?,
    val pathToPlaylistIcon: String?,
    val tracks: String?,
    val numberOfTracks: Int,
    val addedDate: Long = System.currentTimeMillis()
)
