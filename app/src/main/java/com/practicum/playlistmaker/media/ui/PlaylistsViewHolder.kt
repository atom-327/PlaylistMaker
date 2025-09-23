package com.practicum.playlistmaker.media.ui

import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.domain.models.Playlist
import com.practicum.playlistmaker.databinding.PlaylistItemBinding

class PlaylistsViewHolder(private val binding: PlaylistItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Playlist, onPlaylistClick: (Playlist) -> Unit) {
        with(binding) {
            Glide.with(itemView).load(item.pathToPlaylistIcon)
                .placeholder(R.drawable.track_icon_placeholder).transform(
                    CenterCrop(), RoundedCorners(
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            8F,
                            itemView.context.resources.displayMetrics
                        ).toInt()
                    )
                ).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(playlistIcon)
            playlistTitle.text = item.playlistName
            playlistTracks.text = itemView.context.resources.getQuantityString(
                R.plurals.tracks_count,
                item.numberOfTracks,
                item.numberOfTracks
            )
            playlistButton.setOnClickListener {
                onPlaylistClick(item)
            }
        }
    }
}
