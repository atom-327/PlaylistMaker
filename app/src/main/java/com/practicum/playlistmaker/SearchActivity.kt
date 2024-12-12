package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class SearchActivity : AppCompatActivity() {
    private var editTextValue: String = TEXT_VALUE

    companion object {
        const val TEXT_KEY = "TEXT_KEY"
        const val TEXT_VALUE = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbarButton = findViewById<Toolbar>(R.id.toolbarButton)
        val inputEditText = findViewById<EditText>(R.id.searchEditText)
        val clearButton = findViewById<ImageView>(R.id.searchClearIcon)

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

        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideSoftKeyboard(it)
        }

        toolbarButton.setNavigationOnClickListener {
            val returnIntent = Intent(this, MainActivity::class.java)
            startActivity(returnIntent)
            finish()
        }

        val trackList = listOf(
            Track(
                "Smells Like Teen Spirit",
                "Nirvana",
                "5:01",
                "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Billie Jean",
                "Michael Jackson",
                "4:35",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
            ),
            Track(
                "Stayin' Alive",
                "Bee Gees",
                "4:10",
                "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Whole Lotta Love",
                "Led Zeppelin",
                "5:33",
                "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
            ),
            Track(
                "Sweet Child O'Mine",
                "Guns N' Roses",
                "5:03",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
            )
        )
        val recycler = findViewById<RecyclerView>(R.id.rvTrackList)
        recycler.adapter = TrackListAdapter(trackList)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_KEY, editTextValue)
    }

    private fun hideSoftKeyboard(view: View) {
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    class TrackListAdapter(
        private val trackList: List<Track>
    ) : RecyclerView.Adapter<TrackListViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
            return TrackListViewHolder(view)
        }

        override fun onBindViewHolder(holder: TrackListViewHolder, position: Int) {
            holder.bind(trackList[position])
        }

        override fun getItemCount(): Int {
            return trackList.size
        }
    }

    class TrackListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trackIcon: ImageView = itemView.findViewById<ImageView>(R.id.trackIcon)
        private val trackName: TextView = itemView.findViewById<TextView>(R.id.trackName)
        private val trackInfo: TextView = itemView.findViewById<TextView>(R.id.trackInfo)

        fun bind(item: Track) {
            Glide.with(itemView)
                .load(item.artworkUrl100)
                .placeholder(R.drawable.track_icon_placeholder)
                .centerCrop()
                .transform(RoundedCorners(2))
                .into(trackIcon)
            trackName.text = item.trackName
            trackInfo.text =
                itemView.context.getString(R.string.trackInfo, item.artistName, item.trackTime)
        }
    }
}
