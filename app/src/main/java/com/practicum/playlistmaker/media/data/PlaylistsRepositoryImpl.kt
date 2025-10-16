package com.practicum.playlistmaker.media.data

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.practicum.playlistmaker.core.domain.models.Playlist
import com.practicum.playlistmaker.core.domain.models.Track
import com.practicum.playlistmaker.media.data.dao.AddedTrackDao
import com.practicum.playlistmaker.media.data.dao.PlaylistDao
import com.practicum.playlistmaker.media.data.entity.AddedTrackEntity
import com.practicum.playlistmaker.media.data.entity.PlaylistEntity
import com.practicum.playlistmaker.media.data.mapper.AddedTrackDbConvertor
import com.practicum.playlistmaker.media.data.mapper.PlaylistDbConvertor
import com.practicum.playlistmaker.media.domain.api.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlaylistsRepositoryImpl(
    private val playlistDAO: PlaylistDao,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val addedTrackDAO: AddedTrackDao,
    private val addedTrackDbConvertor: AddedTrackDbConvertor,
    private val context: Context
) : PlaylistsRepository {

    override suspend fun addPlaylist(playlist: Playlist, imageUri: Uri?) {
        val imagePath = imageUri?.let { saveImageToPrivateStorage(it) }
        val playlistWithImage = playlist.copy(pathToPlaylistIcon = imagePath)
        val playlistEntity = convertToPlaylistEntity(playlistWithImage).copy(
            addedDate = System.currentTimeMillis()
        )
        playlistDAO.insertPlaylist(playlistEntity)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        playlistDAO.deletePlaylistById(playlistId)
        cleanupOrphanTracks()
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val sortedPlaylists = convertFromPlaylistEntity(
            playlistDAO.getPlaylists().sortedByDescending { it.addedDate })
        emit(sortedPlaylists)
    }

    private fun convertToPlaylistEntity(playlist: Playlist): PlaylistEntity {
        return playlistDbConvertor.map(playlist)
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean {

        val currentTracks = playlist.tracks?.split(",")?.toMutableSet() ?: mutableSetOf()

        if (currentTracks.contains(track.trackId.toString())) {
            return false
        } else {
            val addedTrackEntity = convertToTrackEntity(track).copy(
                addedDate = System.currentTimeMillis()
            )
            addedTrackDAO.insertTrack(addedTrackEntity)

            currentTracks.add(track.trackId.toString())
            val updatedTracks = currentTracks.joinToString(",")

            val updatedPlaylist = playlist.copy(
                tracks = updatedTracks, numberOfTracks = currentTracks.size
            )

            playlistDAO.updatePlaylist(convertToPlaylistEntity(updatedPlaylist))
            return true
        }
    }

    override suspend fun getPlaylistById(playlistId: Int): Playlist? {
        return playlistDAO.getPlaylistById(playlistId)?.let { playlistDbConvertor.map(it) }
    }

    private fun convertToTrackEntity(track: Track): AddedTrackEntity {
        return addedTrackDbConvertor.map(track)
    }

    private fun saveImageToPrivateStorage(uri: Uri): String {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "playlist_cover_$timestamp.jpg"
        val file = File(filePath, fileName)

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                BitmapFactory.decodeStream(inputStream)
                    .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
            }
        }

        return file.absolutePath
    }

    override fun getTracksByPlaylistId(playlistId: Int): Flow<List<Track>> = flow {
        val playlist = playlistDAO.getPlaylistById(playlistId)
        val trackIds = playlist?.tracks?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList()
        if (trackIds.isEmpty()) {
            emit(emptyList())
        } else {
            val trackEntities = addedTrackDAO.getTracksByIds(trackIds)
            val reversedTrackIds = trackIds.reversed()
            val tracks = reversedTrackIds.mapNotNull { trackId ->
                trackEntities.find { it.trackId == trackId }?.let { addedTrackDbConvertor.map(it) }
            }
            emit(tracks)
        }
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Int, playlistId: Int) {
        val playlist = playlistDAO.getPlaylistById(playlistId)
        val currentTracks = playlist!!.tracks?.split(",")?.toMutableList() ?: mutableListOf()
        currentTracks.remove(trackId.toString())
        val updatedTracks = currentTracks.joinToString(",")
        val updatedPlaylist = playlist.copy(
            tracks = updatedTracks, numberOfTracks = currentTracks.size
        )
        playlistDAO.updatePlaylist(updatedPlaylist)
        cleanupOrphanTracks()
    }

    override suspend fun cleanupOrphanTracks() {
        val allPlaylists = playlistDAO.getPlaylists()
        val allTrackIdsInPlaylists = allPlaylists.flatMap { playlist ->
            playlist.tracks?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList()
        }.toSet()
        val allTracks = addedTrackDAO.getTracksByIds(allTrackIdsInPlaylists.toList())
        allTracks.forEach { track ->
            if (!allTrackIdsInPlaylists.contains(track.trackId)) {
                addedTrackDAO.deleteTrackById(track.trackId)
            }
        }
    }

    override fun sharePlaylist(message: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(shareIntent)
    }

    override suspend fun updatePlaylist(playlist: Playlist, imageUri: Uri?) {
        val imagePath = when {
            imageUri == null -> playlist.pathToPlaylistIcon
            isInternalStorageUri(imageUri) -> imageUri.toString()
            else -> saveImageToPrivateStorage(imageUri)
        }
        val playlistWithImage = playlist.copy(pathToPlaylistIcon = imagePath)
        val playlistEntity = convertToPlaylistEntity(playlistWithImage)
        playlistDAO.updatePlaylist(playlistEntity)
    }

    private fun isInternalStorageUri(uri: Uri): Boolean {
        val uriString = uri.toString()
        return uriString.startsWith("/storage/emulated/0/Android/data/${context.packageName}/") ||
                uriString.startsWith(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.path ?: ""
                )
    }
}
