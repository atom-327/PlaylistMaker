package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.SettingsActivity.Companion.SHARED_PREF
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayer : AppCompatActivity() {
    private lateinit var trackIcon: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var playTrackButton: ImageButton
    private lateinit var toLikeTrackButton: ImageButton
    private lateinit var trackTimeToEnd: TextView
    private lateinit var trackTime: TextView
    private lateinit var trackAlbum: TextView
    private lateinit var trackYear: TextView
    private lateinit var trackGenre: TextView
    private lateinit var trackCountry: TextView
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private lateinit var track: Track
    private var isTrackStopped = true
    private var isTrackLicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.audio_player)

        val sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE)
        val searchHistory = SearchHistory(sharedPreferences)
        track = searchHistory.getListeningTrack()!!

        val searchButton = findViewById<androidx.appcompat.widget.Toolbar>(R.id.searchButton)
        trackIcon = findViewById(R.id.trackIcon)
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        playTrackButton = findViewById(R.id.playTrackButton)
        toLikeTrackButton = findViewById(R.id.toLikeTrackButton)
        trackTimeToEnd = findViewById(R.id.trackTimeToEnd)
        trackTime = findViewById(R.id.trackTimeInfo)
        trackAlbum = findViewById(R.id.trackAlbumInfo)
        trackYear = findViewById(R.id.trackYearInfo)
        trackGenre = findViewById(R.id.trackGenreInfo)
        trackCountry = findViewById(R.id.trackCountryInfo)

        Glide.with(this).load(track.getCoverArtwork())
            .placeholder(R.drawable.track_icon_placeholder).centerCrop().transform(
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 8F, resources.displayMetrics
                    ).toInt()
                )
            )
            .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(trackIcon)
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTimeToEnd.text = dateFormat.format(track.trackTimeMillis.toLong())
        trackTime.text = dateFormat.format(track.trackTimeMillis.toLong())
        trackAlbum.text = track.collectionName
        trackYear.text = track.releaseDate.substringBefore("-")
        trackGenre.text = track.primaryGenreName
        trackCountry.text = track.country

        searchButton.setNavigationOnClickListener {
            val returnIntent = Intent(this, SearchActivity::class.java)
            startActivity(returnIntent)
            finish()
        }

        playTrackButton.setOnClickListener {
            val darkTheme = (applicationContext as App).getAppTheme()
            if (isTrackStopped) {
                if (darkTheme) {
                    playTrackButton.setImageResource(R.drawable.to_stop_track_dark)
                } else {
                    playTrackButton.setImageResource(R.drawable.to_stop_track)
                }
                isTrackStopped = false
            } else {
                if (darkTheme) {
                    playTrackButton.setImageResource(R.drawable.to_play_track_dark)
                } else {
                    playTrackButton.setImageResource(R.drawable.to_play_track)
                }
                isTrackStopped = true
            }

        }

        toLikeTrackButton.setOnClickListener {
            if (!isTrackLicked) {
                toLikeTrackButton.setImageResource(R.drawable.track_licked_icon)
                isTrackLicked = true
            } else {
                toLikeTrackButton.setImageResource(R.drawable.to_like_track_icon)
                isTrackLicked = false
            }
        }
    }
}
