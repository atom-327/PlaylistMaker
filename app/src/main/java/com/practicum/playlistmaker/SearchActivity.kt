package com.practicum.playlistmaker

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
import com.practicum.playlistmaker.SettingsActivity.Companion.SHARED_PREF
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private var editTextValue: String = TEXT_VALUE
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit =
        Retrofit.Builder().baseUrl(iTunesBaseUrl).addConverterFactory(GsonConverterFactory.create())
            .build()
    private val itunesService = retrofit.create(ITunesAPI::class.java)
    private lateinit var sharedPreferences: SharedPreferences
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

    companion object {
        const val TRACK_ID = "TRACK_ID"
        const val TEXT_KEY = "TEXT_KEY"
        const val TEXT_VALUE = ""
        private const val CLICK_DEBOUNCE_DELAY = 1_000L
        private const val SEARCH_DEBOUNCE_DELAY = 2_000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE)
        val searchHistory = SearchHistory(sharedPreferences)

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
            val returnIntent = Intent(this, MainActivity::class.java)
            startActivity(returnIntent)
            finish()
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
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    private fun search() {
        if (inputEditText.text.isNotEmpty()) {
            trackList.visibility = View.GONE
            placeholderMessage.visibility = View.GONE
            storyPlaceholder.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            itunesService.search(inputEditText.text.toString())
                .enqueue(object : Callback<ITunesResponse> {
                    override fun onResponse(
                        call: Call<ITunesResponse>, response: Response<ITunesResponse>
                    ) {
                        progressBar.visibility = View.GONE
                        if (response.code() == 200) {
                            tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                trackList.visibility = View.VISIBLE
                                tracks.addAll(response.body()?.results!!)
                                tracksAdapter.notifyDataSetChanged()
                                showMessage("")
                            } else {
                                showMessage(getString(R.string.nothing_found))
                            }
                        } else showMessage(getString(R.string.something_went_wrong))
                    }

                    override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                        progressBar.visibility = View.GONE
                        showMessage(getString(R.string.something_went_wrong))
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
