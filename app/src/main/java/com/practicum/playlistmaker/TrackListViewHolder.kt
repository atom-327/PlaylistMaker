package com.practicum.playlistmaker

import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackIcon: ImageView = itemView.findViewById<ImageView>(R.id.trackIcon)
    private val trackName: TextView = itemView.findViewById<TextView>(R.id.trackName)
    private val trackInfo: TextView = itemView.findViewById<TextView>(R.id.trackInfo)

    fun bind(item: Track) {
        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.track_icon_placeholder)
            .centerCrop()
            .transform(
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        2F, context.resources.displayMetrics
                    ).toInt()
                )
            )
            .into(trackIcon)
        trackName.text = item.trackName
        trackInfo.text =
            itemView.context.getString(R.string.trackInfo, item.artistName, item.trackTime)
    }
}
