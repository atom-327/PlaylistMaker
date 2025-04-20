package com.practicum.playlistmaker.ui.tracks

import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track

class TrackListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackIcon: ImageView = itemView.findViewById(R.id.trackIcon)
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val trackInfo: TextView = itemView.findViewById(R.id.trackInfo)
    private val trackButton: TextView = itemView.findViewById(R.id.trackButton)

    fun bind(item: Track, onTrackClick: (Track) -> Unit) {
        Glide.with(itemView).load(item.artworkUrl100).placeholder(R.drawable.track_icon_placeholder)
            .centerCrop().transform(
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 2F, itemView.context.resources.displayMetrics
                    ).toInt()
                )
            ).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(trackIcon)
        trackName.text = item.trackName
        trackInfo.text = itemView.context.getString(
            R.string.trackInfo, item.artistName, item.trackTimeMillis
        )
        trackButton.setOnClickListener {
            onTrackClick(item)
        }
    }
}
