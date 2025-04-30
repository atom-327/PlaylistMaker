package com.practicum.playlistmaker.ui.tracks

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.SharedPreferencesRepository
import com.practicum.playlistmaker.domain.models.Track

class AudioPlayer : AppCompatActivity() {
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val REFRESH_SECONDS_VALUE_MILLIS = 300L
    }

    private var mediaPlayer = MediaPlayer()
    private val player = Creator.providePlayerInteractor(mediaPlayer)
    private lateinit var sharedPreferencesRepository: SharedPreferencesRepository
    private lateinit var searchHistory: SearchHistoryInteractor
    private var playerState = STATE_DEFAULT
    private var mainThreadHandler: Handler? = null
    private val timerRunnable: Runnable = Runnable { refreshTrackTimer() }
    private lateinit var track: Track
    private var darkTheme: Boolean = false
    private var isTrackLicked = false

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.audio_player)

        mainThreadHandler = Handler(Looper.getMainLooper())
        sharedPreferencesRepository = Creator.getSharedPreferencesRepository()
        searchHistory = Creator.provideSearchHistoryInteractor()
        darkTheme = (applicationContext as App).getAppTheme()
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
            ).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(trackIcon)
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTimeToEnd.text = player.getCurrentPosition()
        trackTime.text = track.trackTimeMillis
        trackAlbum.text = track.collectionName
        trackYear.text = track.releaseDate.substringBefore("-")
        trackGenre.text = track.primaryGenreName
        trackCountry.text = track.country

        searchButton.setNavigationOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        preparePlayer(track.previewUrl)

        playTrackButton.setOnClickListener {
            playbackControl()
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

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        mainThreadHandler?.removeCallbacks(timerRunnable)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun preparePlayer(trackUrl: String) {
        player.prepare(trackUrl)
        mediaPlayer.setOnPreparedListener {
            playTrackButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            if (darkTheme) {
                playTrackButton.setImageResource(R.drawable.to_play_track_dark)
            } else {
                playTrackButton.setImageResource(R.drawable.to_play_track)
            }
            playerState = STATE_PREPARED
            mainThreadHandler?.removeCallbacks(timerRunnable)
            trackTimeToEnd.text = player.resetTimer()
        }
    }

    private fun startPlayer() {
        player.play()
        if (darkTheme) {
            playTrackButton.setImageResource(R.drawable.to_stop_track_dark)
        } else {
            playTrackButton.setImageResource(R.drawable.to_stop_track)
        }
        playerState = STATE_PLAYING
        mainThreadHandler?.post(timerRunnable)
    }

    private fun pausePlayer() {
        player.pause()
        mainThreadHandler?.removeCallbacks(timerRunnable)
        if (darkTheme) {
            playTrackButton.setImageResource(R.drawable.to_play_track_dark)
        } else {
            playTrackButton.setImageResource(R.drawable.to_play_track)
        }
        playerState = STATE_PAUSED
    }

    private fun refreshTrackTimer() {
        if (playerState == STATE_PLAYING) {
            trackTimeToEnd.text = player.getCurrentPosition()
            mainThreadHandler?.postDelayed(timerRunnable, REFRESH_SECONDS_VALUE_MILLIS)
        }
    }
}
