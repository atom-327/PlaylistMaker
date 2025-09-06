package com.practicum.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.core.domain.models.Playlist
import com.practicum.playlistmaker.databinding.PlaylistItemBinding

class PlaylistsAdapter(
    private val playlists: MutableList<Playlist>, private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val binding =
            PlaylistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        val item = playlists[position]
        holder.bind(item, onPlaylistClick)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}
