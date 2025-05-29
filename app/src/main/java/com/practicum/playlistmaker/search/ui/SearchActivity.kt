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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.domain.api.SharedPreferencesRepository
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.player.ui.AudioPlayer
import com.practicum.playlistmaker.presentation.TracksState
import com.practicum.playlistmaker.search.presentation.view_model.SearchViewModel

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val TRACK_ID = "TRACK_ID"
        private const val TEXT_KEY = "TEXT_KEY"
    }

    private lateinit var binding: ActivitySearchBinding

    private val viewModel by lazy {
        ViewModelProvider(
            this, SearchViewModel.getViewModelFactory()
        )[SearchViewModel::class.java]
    }

    private lateinit var sharedPreferencesRepository: SharedPreferencesRepository
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener
    private lateinit var tracksAdapter: TrackListAdapter
    private lateinit var storyTracksAdapter: TrackListAdapter

    private val tracks = mutableListOf<Track>()
    private val storyList = mutableListOf<Track>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesRepository = Creator.getSharedPreferencesRepository()

        if (savedInstanceState != null) {
            viewModel.changedText = savedInstanceState.getString(TEXT_KEY, "")
            binding.searchEditText.setText(viewModel.changedText)
        }

        listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == TRACK_ID) {
                viewModel.loadSearchHistory()
                storyTracksAdapter.notifyDataSetChanged()
            }
        }
        sharedPreferencesRepository.registerOnSharedPreferenceChangeListener(listener)

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
                    binding.storyTracks.visibility = View.GONE
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            searchEditText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && searchEditText.text.isEmpty()) {
                    viewModel.loadSearchHistory()
                    storyTracksAdapter.notifyDataSetChanged()
                    if (storyList.size != 0) {
                        storyTracks.visibility = View.VISIBLE
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
                viewModel.clearSearchHistory(storyList)
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
                viewModel.addToHistory(storyList, track)
                val audioPlayerIntent = Intent(this, AudioPlayer::class.java)
                startActivity(audioPlayerIntent)
            }
        }
        binding.rvTrackList.layoutManager = LinearLayoutManager(this@SearchActivity)
        binding.rvTrackList.adapter = tracksAdapter

        storyTracksAdapter = TrackListAdapter(storyList) { track ->
            if (viewModel.clickDebounce()) {
                viewModel.addToHistory(storyList, track)
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

    private fun render(state: TracksState) {
        when (state) {
            is TracksState.Loading -> showLoading()
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.Empty -> showEmpty(state.emptyMessage)
            is TracksState.Error -> showError(state.errorMessage)
            is TracksState.History -> showHistory(state.storyList)
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

    private fun showHistory(storyList: List<Track>) {
        this.tracks.clear()
        tracksAdapter.notifyDataSetChanged()
        with(binding) {
            placeholderMessage.visibility = View.GONE
            storyTracks.visibility = View.VISIBLE
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

//class SearchActivity : AppCompatActivity() {
//
//    companion object {
//        private const val TRACK_ID = "TRACK_ID"
//        private const val TEXT_KEY = "TEXT_KEY"
////        private const val CLICK_DEBOUNCE_DELAY = 1_000L
////        private const val TEXT_VALUE = ""
////        private const val SEARCH_DEBOUNCE_DELAY = 2_000L
//    }
//
//    private lateinit var binding: ActivitySearchBinding
//
//    private val viewModel by lazy {
//        ViewModelProvider(
//            this, SearchViewModel.getViewModelFactory()
//        )[SearchViewModel::class.java]
//    }
//
//    private lateinit var sharedPreferencesRepository: SharedPreferencesRepository
//    private lateinit var searchHistory: SearchHistoryInteractor
//    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener
//    private lateinit var tracksAdapter: TrackListAdapter
//    private lateinit var storyTracksAdapter: TrackListAdapter
//
//    //    var changedText: String = TEXT_VALUE
//    private var textWatcher: TextWatcher? = null
//
//    private val tracks = mutableListOf<Track>()
//    private var storyTracks = mutableListOf<Track>()
////    private var isClickAllowed = true
////    private val handler = Handler(Looper.getMainLooper())
////    private val searchRunnable = Runnable { search() }
//
////    private lateinit var toolbarButton: Toolbar
////    private lateinit var inputEditText: EditText
////    private lateinit var clearButton: ImageView
////    private lateinit var trackList: RecyclerView
////    private lateinit var placeholderMessage: LinearLayout
////    private lateinit var placeholderImage: ImageView
////    private lateinit var placeholderText: TextView
////    private lateinit var updateButton: Button
////    private lateinit var storyPlaceholder: LinearLayout
////    private lateinit var storyTrackList: RecyclerView
////    private lateinit var clearStoryTracksButton: Button
////    private lateinit var progressBar: ProgressBar
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySearchBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
////        tracksSearchPresenter =
////            (this.applicationContext as? App)?.tracksSearchPresenter
////
////        if (tracksSearchPresenter == null) {
////            tracksSearchPresenter = Creator.provideTracksSearchPresenter(
////                context = this.applicationContext
////            )
////            (this.applicationContext as? App)?.tracksSearchPresenter = tracksSearchPresenter
////        }
////        tracksInteractor = Creator.provideTracksInteractor()
//
//        sharedPreferencesRepository = Creator.getSharedPreferencesRepository()
//        searchHistory = Creator.provideSearchHistoryInteractor()
//
////        toolbarButton = findViewById(R.id.toolbarButton)
////        inputEditText = findViewById(R.id.searchEditText)
////        clearButton = findViewById(R.id.searchClearIcon)
////        trackList = findViewById(R.id.rvTrackList)
////        placeholderMessage = findViewById(R.id.placeholderMessage)
////        placeholderImage = findViewById(R.id.placeholderImage)
////        placeholderText = findViewById(R.id.placeholderText)
////        updateButton = findViewById(R.id.updateButton)
////        storyPlaceholder = findViewById(R.id.storyTracks)
////        storyTrackList = findViewById(R.id.storyTrackList)
////        clearStoryTracksButton = findViewById(R.id.clearStoryTracksButton)
////        progressBar = findViewById(R.id.progressBar)
//
//        binding.updateButton.visibility = View.GONE
//        binding.storyTracks.visibility = View.GONE
//
//        tracksAdapter = TrackListAdapter { track ->
//            if (viewModel.clickDebounce()) {
//                searchHistory.addTrack(storyTracks, track)
//                val audioPlayerIntent = Intent(this, AudioPlayer::class.java)
//                startActivity(audioPlayerIntent)
//            }
//        }
//        binding.rvTrackList.layoutManager =
//            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        binding.rvTrackList.adapter = tracksAdapter
//
//        storyTracksAdapter = TrackListAdapter { track ->
//            if (viewModel.clickDebounce()) {
//                searchHistory.addTrack(storyTracks, track)
//                val audioPlayerIntent = Intent(this, AudioPlayer::class.java)
//                startActivity(audioPlayerIntent)
//            }
//        }
//        binding.storyTrackList.layoutManager =
//            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        binding.storyTrackList.adapter = storyTracksAdapter
//
//        if (savedInstanceState != null) {
//            viewModel.changedText = savedInstanceState.getString(TEXT_KEY, "")
//            binding.searchEditText.setText(viewModel.changedText)
//        }
//
//        object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                binding.searchClearIcon.isVisible = !s.isNullOrEmpty()
//                println(s.toString())
//                viewModel.searchDebounce(changedText = s.toString())
//                if (binding.searchEditText.hasFocus() && s?.isEmpty() == true && storyTracks.isNotEmpty()) {
//                    tracks.clear()
//                    tracksAdapter.notifyDataSetChanged()
//                    binding.placeholderMessage.visibility = View.GONE
//                    binding.storyTracks.visibility = View.VISIBLE
//                } else {
//                    binding.storyTracks.visibility = View.GONE
//                }
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//            }
//        }
//        textWatcher?.let { binding.searchEditText.addTextChangedListener(it) }
//        viewModel.getState().observe(this) {
//            render(it)
//        }
////        binding.searchEditText.addTextChangedListener(simpleTextWatcher)
//
//        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
//            if (hasFocus && binding.searchEditText.text.isEmpty()) {
//                storyTracks = searchHistory.loadTracks()
//                storyTracksAdapter.notifyDataSetChanged()
//                if (storyTracks.size != 0) {
//                    binding.storyTracks.visibility = View.VISIBLE
//                }
//            }
//        }
//
//        binding.clearStoryTracksButton.setOnClickListener {
//            searchHistory.clearHistory(storyTracks)
//            storyTracksAdapter.notifyDataSetChanged()
//            binding.storyTracks.visibility = View.GONE
//        }
//
//        binding.searchClearIcon.setOnClickListener {
//            binding.searchEditText.setText("")
//            hideSoftKeyboard(it)
//            tracks.clear()
//            tracksAdapter.notifyDataSetChanged()
//        }
//
//        binding.toolbarButton.setNavigationOnClickListener {
//            finish()
//            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
//        }
//
////        binding.updateButton.setOnClickListener {
////            viewModel.search()
////        }
//
//        listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
//            if (key == TRACK_ID) {
//                searchHistory.loadTracks(storyTracks)
//                storyTracksAdapter.notifyDataSetChanged()
//            }
//        }
//        sharedPreferencesRepository.registerOnSharedPreferenceChangeListener(listener)
//    }

//    private fun render(state: TracksState) {
//        when (state) {
//            is TracksState.Loading -> showLoading()
//            is TracksState.Content -> showContent(state.tracks)
//            is TracksState.Error -> showError(state.errorMessage)
//        }
//    }
//
//    private fun showLoading() {
//        binding.rvTrackList.visibility = View.GONE
//        binding.placeholderMessage.visibility = View.GONE
//        binding.storyTracks.visibility = View.GONE
//        binding.progressBar.visibility = View.VISIBLE
//    }
//
//    private fun showContent(tracks: List<Track>) {
//        binding.rvTrackList.visibility = View.VISIBLE
//        binding.placeholderMessage.visibility = View.GONE
//        binding.storyTracks.visibility = View.GONE
//        binding.progressBar.visibility = View.GONE
//
//        this.tracks.clear()
//        this.tracks.addAll(tracks)
//        tracksAdapter.notifyDataSetChanged()
//    }
//
//    private fun showError(errorMessage: String) {
//        if (errorMessage.isNotEmpty()) {
//            binding.rvTrackList.visibility = View.GONE
//            binding.placeholderMessage.visibility = View.VISIBLE
//            binding.storyTracks.visibility = View.GONE
//            binding.progressBar.visibility = View.GONE
//            tracks.clear()
//            tracksAdapter.notifyDataSetChanged()
//            binding.placeholderText.text = errorMessage
//            if (errorMessage == getString(R.string.something_went_wrong)) {
//                binding.placeholderImage.setImageResource(R.drawable.something_went_wrong)
//                binding.updateButton.visibility = View.VISIBLE
//            } else {
//                binding.placeholderImage.setImageResource(R.drawable.nothing_found)
//                binding.updateButton.visibility = View.GONE
//            }
//        } else {
//            binding.placeholderMessage.visibility = View.GONE
//        }
//    }
//
////    override fun showPlaceholderMessage(isVisible: Boolean) {
////        placeholderMessage.visibility = if (isVisible) View.VISIBLE else View.GONE
////    }
////
////    override fun showUpdateButton(isVisible: Boolean) {
////        updateButton.visibility = if (isVisible) View.VISIBLE else View.GONE
////    }
////
////    override fun showTracksList(isVisible: Boolean) {
////        trackList.visibility = if (isVisible) View.VISIBLE else View.GONE
////    }
////
////    override fun showStoryTracksList(isVisible: Boolean) {
////        storyTrackList.visibility = if (isVisible) View.VISIBLE else View.GONE
////    }
////
////    override fun showProgressBar(isVisible: Boolean) {
////        progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
////    }
////
////    override fun setPlaceHolderImage(view: Int) {
////        placeholderImage.setImageResource(view)
////    }
////
////    override fun changePlaceholderText(newPlaceholderText: String) {
////        placeholderText.text = newPlaceholderText
////    }
////
////    override fun updateTracksList(newTracksList: List<Track>) {
////        tracksAdapter.tracks.clear()
////        tracksAdapter.tracks.addAll(newTracksList)
////        tracksAdapter.notifyDataSetChanged()
////    }
////
////    private fun search() {
////        if (inputEditText.text.isNotEmpty()) {
////            trackList.visibility = View.GONE
////            placeholderMessage.visibility = View.GONE
////            storyPlaceholder.visibility = View.GONE
////            progressBar.visibility = View.VISIBLE
////
////            tracksInteractor.searchTracks(
////                inputEditText.text.toString(),
////                object : TracksInteractor.TracksConsumer {
////                    override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
////                        handler.post {
////                            progressBar.visibility = View.GONE
////                            tracks.clear()
////                            if (foundTracks != null) {
////                                trackList.visibility = View.VISIBLE
////                                tracks.addAll(foundTracks)
////                                tracksAdapter.notifyDataSetChanged()
////                                showMessage("")
////                            }
////                            if (errorMessage != null) {
////                                showMessage(errorMessage)
////                            }
////                        }
////                    }
////                })
////        }
////    }
////
////    private fun showMessage(text: String) {
////        if (text.isNotEmpty()) {
////            binding.placeholderMessage.visibility = View.VISIBLE
////            tracks.clear()
////            tracksAdapter.notifyDataSetChanged()
////            binding.placeholderText.text = text
////            if (text == getString(R.string.something_went_wrong)) {
////                binding.placeholderImage.setImageResource(R.drawable.something_went_wrong)
////                binding.updateButton.visibility = View.VISIBLE
////            } else {
////                binding.placeholderImage.setImageResource(R.drawable.nothing_found)
////                binding.updateButton.visibility = View.GONE
////            }
////        } else {
////            binding.placeholderMessage.visibility = View.GONE
////        }
////    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putString(TEXT_KEY, viewModel.changedText)
//    }
//
//    private fun hideSoftKeyboard(view: View) {
//        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        manager.hideSoftInputFromWindow(view.windowToken, 0)
//    }
//
////    private fun clickDebounce(): Boolean {
////        val current = isClickAllowed
////        if (isClickAllowed) {
////            isClickAllowed = false
////            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
////        }
////        return current
////    }
////
////    private fun searchDebounce() {
////        handler.removeCallbacks(searchRunnable)
////        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
////    }
//}
