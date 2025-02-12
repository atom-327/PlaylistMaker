package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private val tracks = ArrayList<Track>()
    private val adapter = TrackListAdapter()

    private lateinit var toolbarButton: Toolbar
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var trackList: RecyclerView
    private lateinit var placeholderMessage: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var updateButton: Button

    companion object {
        const val TEXT_KEY = "TEXT_KEY"
        const val TEXT_VALUE = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        toolbarButton = findViewById(R.id.toolbarButton)
        inputEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.searchClearIcon)
        trackList = findViewById(R.id.rvTrackList)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderText = findViewById(R.id.placeholderText)
        updateButton = findViewById(R.id.updateButton)

        updateButton.visibility = View.GONE

        adapter.tracks = tracks

        trackList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackList.adapter = adapter

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
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideSoftKeyboard(it)
            tracks.clear()
        }

        toolbarButton.setNavigationOnClickListener {
            val returnIntent = Intent(this, MainActivity::class.java)
            startActivity(returnIntent)
            finish()
        }

        updateButton.setOnClickListener {
            search()
        }
    }

    private fun search() {
        itunesService.search(inputEditText.text.toString())
            .enqueue(object : Callback<ITunesResponse> {
                override fun onResponse(
                    call: Call<ITunesResponse>, response: Response<ITunesResponse>
                ) {
                    if (response.code() == 200) {
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.results!!)
                            adapter.notifyDataSetChanged()
                            showMessage("")
                        } else {
                            showMessage(getString(R.string.nothing_found))
                        }
                    } else showMessage(getString(R.string.something_went_wrong))
                }

                override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                    showMessage(getString(R.string.something_went_wrong))
                }
            })
    }

    private fun showMessage(text: String) {
        if (text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            tracks.clear()
            adapter.notifyDataSetChanged()
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
}
