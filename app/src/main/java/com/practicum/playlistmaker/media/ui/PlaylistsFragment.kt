package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.domain.models.Playlist
import com.practicum.playlistmaker.core.ui.root.RootActivity
import com.practicum.playlistmaker.core.util.debounce
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.media.presentation.PlaylistsState
import com.practicum.playlistmaker.media.presentation.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel

class PlaylistsFragment : Fragment() {

    private lateinit var viewModel: PlaylistsViewModel
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit
    private lateinit var playlistsAdapter: PlaylistsAdapter
    private val playlists = mutableListOf<Playlist>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = getViewModel()

        onPlaylistClickDebounce = debounce<Playlist>(
            PlaylistsFragment.Companion.CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlist ->
            val bundle = Bundle().apply {
                putInt("playlistId", playlist.playlistId)
            }
            findNavController().navigate(
                R.id.action_mediaFragment_to_playlistInfoFragment,
                bundle
            )
        }

        setupViews()

        viewModel.fillData()

        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            createPlaylistButton.setOnClickListener {
                findNavController().navigate(
                    R.id.action_mediaFragment_to_playlistMakerFragment
                )
            }

            playlistsAdapter = PlaylistsAdapter(playlists) { playlist ->
                (activity as RootActivity).animateBottomNavigationView()
                onPlaylistClickDebounce(playlist)
            }
            rvPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)
            rvPlaylists.adapter = playlistsAdapter
        }
    }

    private fun setupObservers() {
        viewModel.playlistStateLiveData().observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Loading -> showLoading()
            is PlaylistsState.Content -> showContent(state.playlists)
            is PlaylistsState.Empty -> showEmpty()
        }
    }

    private fun showLoading() {
        with(binding) {
            rvPlaylists.isVisible = false
            placeholderMessage.isVisible = false
            progressBar.isVisible = true
        }
    }

    private fun showContent(playlists: List<Playlist>) {
        with(binding) {
            rvPlaylists.isVisible = true
            placeholderMessage.isVisible = false
            progressBar.isVisible = false
        }
        this.playlists.clear()
        this.playlists.addAll(playlists)
        playlistsAdapter.notifyDataSetChanged()
    }

    private fun showEmpty() {
        with(binding) {
            rvPlaylists.isVisible = false
            placeholderMessage.isVisible = true
            progressBar.isVisible = false
            playlists.clear()
            playlistsAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1_000L
        fun newInstance() = PlaylistsFragment()
    }
}
