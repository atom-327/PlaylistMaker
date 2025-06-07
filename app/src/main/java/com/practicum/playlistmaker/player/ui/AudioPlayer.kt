package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.core.creator.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.AudioPlayerBinding
import com.practicum.playlistmaker.core.domain.api.SharedPreferencesRepository
import com.practicum.playlistmaker.core.domain.models.Track
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.presentation.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.core.ui.App

class AudioPlayer : AppCompatActivity() {

    private lateinit var binding: AudioPlayerBinding

    private val viewModel by lazy {
        ViewModelProvider(
            this, AudioPlayerViewModel.getViewModelFactory()
        )[AudioPlayerViewModel::class.java]
    }

    private lateinit var player: PlayerInteractor
    private lateinit var searchHistory: SearchHistoryInteractor
    private lateinit var sharedPreferencesRepository: SharedPreferencesRepository

    private lateinit var track: Track
    private var darkTheme: Boolean = false
    private var isTrackLicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        player = Creator.providePlayerInteractor()
        searchHistory = Creator.provideSearchHistoryInteractor()
        sharedPreferencesRepository = Creator.getSharedPreferencesRepository()
        darkTheme = (applicationContext as App).getAppTheme()
        track = searchHistory.getListeningTrack()!!

        setupTrackInfo(track, this)
        setupViews()
        setupObservers()
    }

    private fun setupTrackInfo(track: Track, audioPlayer: AudioPlayer) {
        with(binding) {
            Glide.with(audioPlayer).load(track.getCoverArtwork())
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
            trackTimeInfo.text = track.trackTimeMillis
            trackAlbumInfo.text = track.collectionName
            trackYearInfo.text = track.releaseDate.substringBefore("-")
            trackGenreInfo.text = track.primaryGenreName
            trackCountryInfo.text = track.country
        }
    }

    private fun setupViews() {
        with(binding) {
            searchButton.setNavigationOnClickListener {
                finish()
                overridePendingTransition(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
                )
            }

            viewModel.preparePlayer(track.previewUrl)

            playTrackButton.setOnClickListener {
                viewModel.playbackControl()
            }

            toLikeTrackButton.setOnClickListener {
                changeLickedButtonStyle()
            }
        }
    }

    private fun setupObservers() {
        viewModel.getState().observe(this) { state ->
            render(state)
        }

        viewModel.getPosition().observe(this) { position ->
            binding.trackTimeToEnd.text = position
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    private fun render(state: Int) {
        when (state) {
            0 -> preparePlayer()
            1, 3 -> startPlayer()
            2 -> pausePlayer()
        }
    }

    private fun preparePlayer() {
        binding.playTrackButton.isEnabled = true
        binding.trackTimeToEnd.text = player.resetTimer()
    }

    private fun startPlayer() {
        changePlayButtonStyle()
    }

    private fun pausePlayer() {
        changePauseButtonStyle()
    }

    private fun changePlayButtonStyle() {
        if (darkTheme) {
            binding.playTrackButton.setImageResource(R.drawable.to_play_track_dark)
        } else {
            binding.playTrackButton.setImageResource(R.drawable.to_play_track)
        }
    }

    private fun changePauseButtonStyle() {
        if (darkTheme) {
            binding.playTrackButton.setImageResource(R.drawable.to_stop_track_dark)
        } else {
            binding.playTrackButton.setImageResource(R.drawable.to_stop_track)
        }
    }

    private fun changeLickedButtonStyle() {
        if (!isTrackLicked) {
            binding.toLikeTrackButton.setImageResource(R.drawable.track_licked_icon)
            isTrackLicked = true
        } else {
            binding.toLikeTrackButton.setImageResource(R.drawable.to_like_track_icon)
            isTrackLicked = false
        }
    }
}
