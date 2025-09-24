package com.practicum.playlistmaker.media.ui

import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.domain.models.Playlist
import com.practicum.playlistmaker.databinding.PlaylistAudioViewItemBinding

class PlaylistAudioViewHolder(private val binding: PlaylistAudioViewItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Playlist, onPlaylistClick: (Playlist) -> Unit) {
        with(binding) {
            Glide.with(itemView).load(item.pathToPlaylistIcon)
                .placeholder(R.drawable.track_icon_placeholder).transform(
                    CenterCrop(), RoundedCorners(
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            2F,
                            itemView.context.resources.displayMetrics
                        ).toInt()
                    )
                ).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(playlistIcon)
            playlistName.text = item.playlistName
            playlistInfo.text = itemView.context.resources.getQuantityString(
                R.plurals.tracks_count,
                item.numberOfTracks,
                item.numberOfTracks
            )
//            playlistInfo.text = getTrackWordForm(item.numberOfTracks)
            playlistButton.setOnClickListener {
                onPlaylistClick(item)
            }
        }
    }

//    private fun getTrackWordForm(count: Int): String {
//        return itemView.context.resources.getQuantityString(
//            R.plurals.tracks_count,
//            count,
//            count
//        )
//    }

//    private fun getTrackWordForm(count: Int): String {
//        return when {
//            count % 10 == 1 && count % 100 != 11 -> "$count трек"
//            count % 10 in 2..4 && count % 100 !in 12..14 -> "$count трека"
//            else -> "$count треков"
//        }
//    }
}
