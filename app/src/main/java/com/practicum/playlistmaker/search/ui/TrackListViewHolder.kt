package com.practicum.playlistmaker.search.ui

import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackItemBinding
import com.practicum.playlistmaker.core.domain.models.Track

class TrackListViewHolder(private val binding: TrackItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Track, onTrackClick: (Track) -> Unit) {
        with(binding) {
            Glide.with(itemView).load(item.artworkUrl100)
                .placeholder(R.drawable.track_icon_placeholder).centerCrop().transform(
                    RoundedCorners(
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            2F,
                            itemView.context.resources.displayMetrics
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
}
