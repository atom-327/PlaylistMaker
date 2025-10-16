package com.practicum.playlistmaker.media.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.media.data.entity.AddedTrackEntity

@Dao
interface AddedTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: AddedTrackEntity)

    @Query("DELETE FROM added_track_table WHERE trackId = :trackId")
    suspend fun deleteTrackById(trackId: Int)

    @Query("SELECT * FROM added_track_table WHERE trackId IN (:trackIds)")
    suspend fun getTracksByIds(trackIds: List<Int>): List<AddedTrackEntity>
}
