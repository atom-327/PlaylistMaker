package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackListAdapter : RecyclerView.Adapter<TrackListViewHolder>() {
    companion object {
        const val SHARED_PREF = "ThemePrefs"
        const val TRACK_ID = "TRACK_ID"
    }

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackListViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackListViewHolder, position: Int) {
        val item = tracks[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            val searchActivity = SearchActivity()
            searchActivity.addTrack(item)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}
