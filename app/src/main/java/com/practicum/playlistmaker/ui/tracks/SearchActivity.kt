package com.practicum.playlistmaker.ui.tracks

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.SharedPreferencesRepository
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.Track

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val TRACK_ID = "TRACK_ID"
        private const val TEXT_KEY = "TEXT_KEY"
        private const val TEXT_VALUE = ""
        private const val CLICK_DEBOUNCE_DELAY = 1_000L
        private const val SEARCH_DEBOUNCE_DELAY = 2_000L
    }

    private var editTextValue: String = TEXT_VALUE

    private lateinit var sharedPreferencesRepository: SharedPreferencesRepository
    private lateinit var searchHistory: SearchHistoryInteractor
    private lateinit var tracksInteractor: TracksInteractor
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener

    private val tracks = mutableListOf<Track>()
    private val storyTracks = mutableListOf<Track>()
    private lateinit var tracksAdapter: TrackListAdapter
    private lateinit var storyTracksAdapter: TrackListAdapter
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }

    private lateinit var toolbarButton: Toolbar
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var trackList: RecyclerView
    private lateinit var placeholderMessage: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var updateButton: Button
    private lateinit var storyPlaceholder: LinearLayout
    private lateinit var storyTrackList: RecyclerView
    private lateinit var clearStoryTracksButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        tracksInteractor = Creator.provideTracksInteractor()
        sharedPreferencesRepository = Creator.getSharedPreferencesRepository()
        searchHistory = Creator.provideSearchHistoryInteractor()

        toolbarButton = findViewById(R.id.toolbarButton)
        inputEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.searchClearIcon)
        trackList = findViewById(R.id.rvTrackList)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderText = findViewById(R.id.placeholderText)
        updateButton = findViewById(R.id.updateButton)
        storyPlaceholder = findViewById(R.id.storyTracks)
        storyTrackList = findViewById(R.id.storyTrackList)
        clearStoryTracksButton = findViewById(R.id.clearStoryTracksButton)
        progressBar = findViewById(R.id.progressBar)

        updateButton.visibility = View.GONE
        storyPlaceholder.visibility = View.GONE

        tracksAdapter = TrackListAdapter(tracks) { track ->
            if (clickDebounce()) {
                searchHistory.addTrack(storyTracks, track)
                val audioPlayerIntent = Intent(this, AudioPlayer::class.java)
                startActivity(audioPlayerIntent)
            }
        }
        trackList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackList.adapter = tracksAdapter

        storyTracksAdapter = TrackListAdapter(storyTracks) { track ->
            if (clickDebounce()) {
                searchHistory.addTrack(storyTracks, track)
                val audioPlayerIntent = Intent(this, AudioPlayer::class.java)
                startActivity(audioPlayerIntent)
            }
        }
        storyTrackList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        storyTrackList.adapter = storyTracksAdapter

        if (savedInstanceState != null) {
            editTextValue = savedInstanceState.getString(TEXT_KEY, TEXT_VALUE)
            inputEditText.setText(editTextValue)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                println(s.toString())
                editTextValue = s.toString()
                searchDebounce()
                if (inputEditText.hasFocus() && s?.isEmpty() == true && storyTracks.isNotEmpty()) {
                    tracks.clear()
                    tracksAdapter.notifyDataSetChanged()
                    placeholderMessage.visibility = View.GONE
                    storyPlaceholder.visibility = View.VISIBLE
                } else {
                    storyPlaceholder.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                searchHistory.loadTracks(storyTracks)
                storyTracksAdapter.notifyDataSetChanged()
                if (storyTracks.size != 0) {
                    storyPlaceholder.visibility = View.VISIBLE
                }
            }
        }

        clearStoryTracksButton.setOnClickListener {
            searchHistory.clearHistory(storyTracks)
            storyTracksAdapter.notifyDataSetChanged()
            storyPlaceholder.visibility = View.GONE
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideSoftKeyboard(it)
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
        }

        toolbarButton.setNavigationOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        updateButton.setOnClickListener {
            search()
        }

        listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == TRACK_ID) {
                searchHistory.loadTracks(storyTracks)
                storyTracksAdapter.notifyDataSetChanged()
            }
        }
        sharedPreferencesRepository.registerOnSharedPreferenceChangeListener(listener)
    }

    private fun search() {
        if (inputEditText.text.isNotEmpty()) {
            trackList.visibility = View.GONE
            placeholderMessage.visibility = View.GONE
            storyPlaceholder.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            tracksInteractor.searchTracks(inputEditText.text.toString(),
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>) {
                        handler.post {
                            progressBar.visibility = View.GONE
                            tracks.clear()
                            if (foundTracks.isNotEmpty()) {
                                trackList.visibility = View.VISIBLE
                                tracks.addAll(foundTracks)
                                tracksAdapter.notifyDataSetChanged()
                                showMessage("")
                            } else {
                                showMessage(getString(R.string.nothing_found))
                            }
                        }
                    }
                })
        }
    }

    private fun showMessage(text: String) {
        if (text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            placeholderText.text = text
            if (text == getString(R.string.something_went_wrong)) {
                placeholderImage.setImageResource(R.drawable.something_went_wrong)
                updateButton.visibility = View.VISIBLE
            } else {
                placeholderImage.setImageResource(R.drawable.nothing_found)
                updateButton.visibility = View.GONE
            }
        } else {
            placeholderMessage.visibility = View.GONE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_KEY, editTextValue)
    }

    private fun hideSoftKeyboard(view: View) {
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
}
