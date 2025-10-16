package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.domain.models.Track
import com.practicum.playlistmaker.core.ui.root.RootActivity
import com.practicum.playlistmaker.core.util.debounce
import com.practicum.playlistmaker.databinding.FragmentFavoritesBinding
import com.practicum.playlistmaker.media.presentation.FavouritesState
import com.practicum.playlistmaker.media.presentation.view_model.FavouritesViewModel
import com.practicum.playlistmaker.search.ui.TrackListAdapter
import org.koin.androidx.viewmodel.ext.android.getViewModel

class FavouritesFragment : Fragment() {

    private lateinit var viewModel: FavouritesViewModel
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var favouritesAdapter: TrackListAdapter
    private val tracks = mutableListOf<Track>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = getViewModel()

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false
        ) { track ->
            viewModel.addTrack(track)
            findNavController().navigate(
                R.id.action_mediaFragment_to_audioFragment,
            )
        }

        setupViews()

        viewModel.fillData()

        setupObservers()
    }

    private fun setupViews() {
        favouritesAdapter = TrackListAdapter(
            tracks,
            onTrackClick = { track ->
                (activity as RootActivity).animateBottomNavigationView()
                onTrackClickDebounce(track)
            },
            onTrackLongClick = { track ->
            }
        )
        binding.rvTrackList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvTrackList.adapter = favouritesAdapter
    }

    private fun setupObservers() {
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun render(state: FavouritesState) {
        when (state) {
            is FavouritesState.Loading -> showLoading()
            is FavouritesState.Content -> showContent(state.tracks)
            is FavouritesState.Empty -> showEmpty()
        }
    }

    private fun showLoading() {
        with(binding) {
            rvTrackList.isVisible = false
            placeholderMessage.isVisible = false
            progressBar.isVisible = true
        }
    }

    private fun showContent(tracks: List<Track>) {
        with(binding) {
            rvTrackList.isVisible = true
            placeholderMessage.isVisible = false
            progressBar.isVisible = false
        }
        this.tracks.clear()
        this.tracks.addAll(tracks)
        favouritesAdapter.notifyDataSetChanged()
    }

    private fun showEmpty() {
        with(binding) {
            rvTrackList.isVisible = false
            placeholderMessage.isVisible = true
            progressBar.isVisible = false
            tracks.clear()
            favouritesAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1_000L
        fun newInstance() = FavouritesFragment()
    }
}
