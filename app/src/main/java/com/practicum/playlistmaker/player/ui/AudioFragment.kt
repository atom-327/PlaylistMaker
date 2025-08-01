package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.domain.models.Track
import com.practicum.playlistmaker.core.ui.App
import com.practicum.playlistmaker.databinding.FragmentAudioBinding
import com.practicum.playlistmaker.player.presentation.view_model.AudioPlayerViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel

class AudioFragment : Fragment() {
    private var _binding: FragmentAudioBinding? = null
    private val binding get() = _binding!!
    private var darkTheme: Boolean = false
    private var track: Track? = null
    private var state = -1
    private var isTrackLicked = false
    private lateinit var viewModel: AudioPlayerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAudioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = getViewModel()

        darkTheme = (requireContext().applicationContext as App).getAppTheme()

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {

            searchButton.setNavigationOnClickListener {
                findNavController().navigateUp()
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
        viewModel.getState().observe(viewLifecycleOwner) {
            if (track != it.track && it.track != null) {
                setupTrackInfo(it.track, this)
                track = it.track
            }
            if (state != it.state) {
                render(it.state)
                state = it.state
            }
            binding.trackTimeToEnd.text = it.timer
            binding.playTrackButton.isEnabled = it.isPlayButtonEnabled
            if (isTrackLicked != it.isTrackLicked) {
                changeLickedButtonStyle(it.isTrackLicked)
                isTrackLicked = it.isTrackLicked
            }
        }
    }

    private fun setupTrackInfo(track: Track, audioPlayer: AudioFragment) {
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

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
