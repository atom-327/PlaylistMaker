package com.practicum.playlistmaker.media.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.practicum.playlistmaker.media.data.entity.AddedTrackEntity

@Dao
interface AddedTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: AddedTrackEntity)

    @Delete
    suspend fun deleteTrack(track: AddedTrackEntity)
}
