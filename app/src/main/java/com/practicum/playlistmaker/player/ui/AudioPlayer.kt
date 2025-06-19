package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.AudioPlayerBinding
import com.practicum.playlistmaker.core.domain.models.Track
import com.practicum.playlistmaker.player.presentation.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.core.ui.App
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AudioPlayer : AppCompatActivity() {

    private lateinit var binding: AudioPlayerBinding

    private lateinit var track: Track
    private var darkTheme: Boolean = false

    private val playerInteractor: PlayerInteractor by inject()
    private val searchHistoryInteractor: SearchHistoryInteractor by inject()
    private lateinit var trackUrl: String

    private val viewModel: AudioPlayerViewModel by viewModel {
        parametersOf(playerInteractor, trackUrl)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        track = searchHistoryInteractor.getListeningTrack()!!
        trackUrl = track.previewUrl
        darkTheme = (applicationContext as App).getAppTheme()

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
                    android.R.anim.slide_in_left, android.R.anim.slide_out_right
                )
            }

            playTrackButton.setOnClickListener {
                viewModel.playbackControl()
            }

            toLikeTrackButton.setOnClickListener {
                viewModel.changeLickedButtonStyle()
            }
        }
    }

    private fun setupObservers() {
        viewModel.getState().observe(this) {
            render(it.state)
            binding.trackTimeToEnd.text = it.timer
            binding.playTrackButton.isEnabled = it.isPlayButtonEnabled
            changeLickedButtonStyle(it.isTrackLicked)
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
            1 -> startPlayer()
            2 -> pausePlayer()
            3 -> startPlayer()
        }
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

    private fun changeLickedButtonStyle(isTrackLicked: Boolean) {
        if (isTrackLicked) {
            binding.toLikeTrackButton.setImageResource(R.drawable.track_licked_icon)
        } else {
            binding.toLikeTrackButton.setImageResource(R.drawable.to_like_track_icon)
        }
    }
}
