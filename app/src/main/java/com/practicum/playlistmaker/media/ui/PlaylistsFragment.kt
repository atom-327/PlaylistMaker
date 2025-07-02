package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.media.presentation.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel

class PlaylistsFragment : Fragment() {

    companion object {
        fun newInstance() = PlaylistsFragment()
    }

    private lateinit var viewModel: PlaylistsViewModel

    private lateinit var binding: FragmentPlaylistsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = getViewModel()

        binding.placeholderMessage.visibility = View.GONE

        showEmpty()
    }

    private fun showEmpty() {
        with(binding) {
            placeholderMessage.visibility = View.VISIBLE
            placeholderImage.setImageResource(R.drawable.nothing_found)
            placeholderText.text = getString(R.string.playlists_is_empty)
        }
    }
}
