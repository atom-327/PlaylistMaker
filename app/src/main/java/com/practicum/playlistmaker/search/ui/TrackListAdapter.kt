package com.practicum.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.TrackItemBinding
import com.practicum.playlistmaker.core.domain.models.Track

class TrackListAdapter(
    private val tracks: MutableList<Track>,
    private val onTrackClick: (Track) -> Unit,
    private val onTrackLongClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListViewHolder {
        val binding = TrackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackListViewHolder, position: Int) {
        val item = tracks[position]
        holder.bind(item, onTrackClick, onTrackLongClick)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}
