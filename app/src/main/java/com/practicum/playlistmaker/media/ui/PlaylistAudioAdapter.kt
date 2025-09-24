package com.practicum.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.core.domain.models.Playlist
import com.practicum.playlistmaker.databinding.PlaylistAudioViewItemBinding

class PlaylistAudioAdapter(
    private val playlists: MutableList<Playlist>, private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistAudioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistAudioViewHolder {
        val binding =
            PlaylistAudioViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistAudioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistAudioViewHolder, position: Int) {
        val item = playlists[position]
        holder.bind(item, onPlaylistClick)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}
