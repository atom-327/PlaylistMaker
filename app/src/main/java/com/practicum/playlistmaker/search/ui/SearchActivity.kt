package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.core.domain.models.Track
import com.practicum.playlistmaker.player.ui.AudioPlayer
import com.practicum.playlistmaker.search.presentation.TracksState
import com.practicum.playlistmaker.search.presentation.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val TRACK_ID = "TRACK_ID"
        private const val TEXT_KEY = "TEXT_KEY"
    }

    private lateinit var binding: ActivitySearchBinding

    private lateinit var errorMessage: String
    private lateinit var emptyMessage: String

    private val viewModel: SearchViewModel by viewModel {
        parametersOf(errorMessage, emptyMessage)
    }

    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener
    private lateinit var tracksAdapter: TrackListAdapter
    private lateinit var storyTracksAdapter: TrackListAdapter

    private val tracks = mutableListOf<Track>()
    private val storyList = mutableListOf<Track>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        errorMessage = getString(R.string.something_went_wrong)
        emptyMessage = getString(R.string.nothing_found)

        if (savedInstanceState != null) {
            viewModel.changedText = savedInstanceState.getString(TEXT_KEY, "")
            binding.searchEditText.setText(viewModel.changedText)
        }

        listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == TRACK_ID) {
                viewModel.loadTracks(storyList)
                storyTracksAdapter.notifyDataSetChanged()
            }
        }
        viewModel.registerOnSharedPreferenceChangeListener(listener)

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            updateButton.visibility = View.GONE
            storyTracks.visibility = View.GONE

            toolbarButton.setNavigationOnClickListener {
                finish()
                overridePendingTransition(
                    android.R.anim.slide_in_left, android.R.anim.slide_out_right
                )
            }

            searchEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    searchClearIcon.isVisible = !s.isNullOrEmpty()
                    println(s.toString())
                    viewModel.searchDebounce(s.toString())
                    if (searchEditText.hasFocus() && s?.isEmpty() == true && storyList.isNotEmpty()) {
                        showHistory()
                    } else {
                        storyTracks.visibility = View.GONE
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            searchEditText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && searchEditText.text.isEmpty()) {
                    viewModel.loadTracks(storyList)
                    storyTracksAdapter.notifyDataSetChanged()
                    if (storyList.size != 0) {
                        showHistory()
                    }
                }
            }

            searchClearIcon.setOnClickListener {
                searchEditText.setText("")
                hideSoftKeyboard(it)
                tracks.clear()
                tracksAdapter.notifyDataSetChanged()
            }

            clearStoryTracksButton.setOnClickListener {
                viewModel.clearHistory(storyList)
                storyTracksAdapter.notifyDataSetChanged()
                storyTracks.visibility = View.GONE
            }

            updateButton.setOnClickListener {
                viewModel.search()
            }

            setupRecyclerView()
        }
    }

    private fun setupRecyclerView() {
        tracksAdapter = TrackListAdapter(tracks) { track ->
            if (viewModel.clickDebounce()) {
                viewModel.addTrack(storyList, track)
                val audioPlayerIntent = Intent(this, AudioPlayer::class.java)
                startActivity(audioPlayerIntent)
            }
        }
        binding.rvTrackList.layoutManager = LinearLayoutManager(this@SearchActivity)
        binding.rvTrackList.adapter = tracksAdapter

        storyTracksAdapter = TrackListAdapter(storyList) { track ->
            if (viewModel.clickDebounce()) {
                viewModel.addTrack(storyList, track)
                val audioPlayerIntent = Intent(this, AudioPlayer::class.java)
                startActivity(audioPlayerIntent)
            }
        }
        binding.storyTrackList.layoutManager = LinearLayoutManager(this@SearchActivity)
        binding.storyTrackList.adapter = storyTracksAdapter
    }

    private fun setupObservers() {
        viewModel.getState().observe(this) { state ->
            render(state)
        }
    }

    private fun showHistory() {
        this.tracks.clear()
        tracksAdapter.notifyDataSetChanged()
        with(binding) {
            placeholderMessage.visibility = View.GONE
            storyTracks.visibility = View.VISIBLE
        }
    }

    private fun render(state: TracksState) {
        when (state) {
            is TracksState.Loading -> showLoading()
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.Empty -> showEmpty(state.emptyMessage)
            is TracksState.Error -> showError(state.errorMessage)
        }
    }

    private fun showLoading() {
        with(binding) {
            rvTrackList.visibility = View.GONE
            placeholderMessage.visibility = View.GONE
            storyTracks.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun showContent(tracks: List<Track>) {
        with(binding) {
            rvTrackList.visibility = View.VISIBLE
            placeholderMessage.visibility = View.GONE
            storyTracks.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
        this.tracks.clear()
        this.tracks.addAll(tracks)
        tracksAdapter.notifyDataSetChanged()
    }

    private fun showEmpty(emptyMessage: String) {
        with(binding) {
            rvTrackList.visibility = View.GONE
            placeholderMessage.visibility = View.VISIBLE
            storyTracks.visibility = View.GONE
            progressBar.visibility = View.GONE
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            placeholderText.text = emptyMessage
            placeholderImage.setImageResource(R.drawable.nothing_found)
            updateButton.visibility = View.GONE
        }
    }

    private fun showError(errorMessage: String) {
        with(binding) {
            rvTrackList.visibility = View.GONE
            placeholderMessage.visibility = View.VISIBLE
            storyTracks.visibility = View.GONE
            progressBar.visibility = View.GONE
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            placeholderText.text = errorMessage
            placeholderImage.setImageResource(R.drawable.something_went_wrong)
            updateButton.visibility = View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_KEY, viewModel.changedText)
    }

    private fun hideSoftKeyboard(view: View) {
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
