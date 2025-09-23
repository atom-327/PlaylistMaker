package com.practicum.playlistmaker.media.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.domain.models.Playlist
import com.practicum.playlistmaker.databinding.FragmentPlaylistMakerBinding
import com.practicum.playlistmaker.media.presentation.view_model.PlaylistsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel

class PlaylistMakerFragment : Fragment() {

    private var _binding: FragmentPlaylistMakerBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PlaylistsViewModel
    val requester = PermissionRequester.instance()
    private var isPhotoChanged = false
    private val playlist: Playlist = Playlist(0, null, null, null, null, 0)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaylistMakerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = getViewModel()

        setupViews()
    }

    private fun setupViews() {
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Glide.with(requireContext()).load(uri).centerCrop().transform(
                        RoundedCorners(
                            TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, 8F, resources.displayMetrics
                            ).toInt()
                        )
                    ).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                        .into(binding.addPhotoButton)
                    isPhotoChanged = true
                    playlist.pathToPlaylistIcon = uri.toString()
                }
            }

        with(binding) {
            backButton.setNavigationOnClickListener {
                if (isPhotoChanged || nameOfPlaylist.text.isNotEmpty() || descriptionOfPlaylist.text.isNotEmpty()) {
                    MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.message_of_playlist_dialog))
                        .setMessage(getString(R.string.message_text_of_playlist_dialog))
                        .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                        }.setPositiveButton(R.string.complete) { dialog, which ->
                            findNavController().navigateUp()
                        }.show()
                } else findNavController().navigateUp()
            }

            nameOfPlaylist.doOnTextChanged { text, _, _, _ ->
                val trimmedText = text?.toString()?.trim()
                val isTextValid = !trimmedText.isNullOrEmpty() && trimmedText.isNotBlank()
                val hasFocus = nameOfPlaylist.hasFocus()

                updateEditTextState(
                    editText = nameOfPlaylist,
                    title = titleNameOfPlaylist,
                    hasFocus = hasFocus,
                    isTextValid = isTextValid
                )

                createPlaylistButton.isEnabled = isTextValid
                binding.createPlaylistButton.isClickable = isTextValid

                if (isTextValid) {
                    createPlaylistButton.setBackgroundResource(R.drawable.button_pressed)
                } else {
                    createPlaylistButton.setBackgroundResource(R.drawable.button_enabled)
                }
            }

            nameOfPlaylist.setOnFocusChangeListener { _, hasFocus ->
                val text = nameOfPlaylist.text?.toString()?.trim()
                val isTextValid = !text.isNullOrEmpty() && text.isNotBlank()
                updateEditTextState(
                    editText = nameOfPlaylist,
                    title = titleNameOfPlaylist,
                    hasFocus = hasFocus,
                    isTextValid = isTextValid
                )
                if (!hasFocus) {
                    nameOfPlaylist.setText(text)
                }
            }

            descriptionOfPlaylist.doOnTextChanged { text, _, _, _ ->
                val trimmedText = text?.toString()?.trim()
                val isTextValid = !trimmedText.isNullOrEmpty() && trimmedText.isNotBlank()
                val hasFocus = descriptionOfPlaylist.hasFocus()

                updateEditTextState(
                    editText = descriptionOfPlaylist,
                    title = titleDescriptionOfPlaylist,
                    hasFocus = hasFocus,
                    isTextValid = isTextValid
                )
            }

            descriptionOfPlaylist.setOnFocusChangeListener { _, hasFocus ->
                val text = descriptionOfPlaylist.text?.toString()?.trim()
                val isTextValid = !text.isNullOrEmpty() && text.isNotBlank()
                updateEditTextState(
                    editText = descriptionOfPlaylist,
                    title = titleDescriptionOfPlaylist,
                    hasFocus = hasFocus,
                    isTextValid = isTextValid
                )
                if (!hasFocus) {
                    descriptionOfPlaylist.setText(text)
                }
            }

            addPhotoButton.setOnClickListener {
                lifecycleScope.launch {
                    requester.request(Manifest.permission.READ_MEDIA_IMAGES).collect { result ->
                        when (result) {
                            is PermissionResult.Granted -> {
                                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }

                            is PermissionResult.Denied.DeniedPermanently -> {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.data = Uri.fromParts("package", context?.packageName, null)
                                context?.startActivity(intent)
                            }

                            is PermissionResult.Denied.NeedsRationale -> {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.access_point_to_the_gallery),
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            is PermissionResult.Cancelled -> {
                                return@collect
                            }
                        }
                    }
                }
            }

            createPlaylistButton.setOnClickListener {
                playlist.playlistName = nameOfPlaylist.text.toString().trim()
                playlist.playlistDescription = descriptionOfPlaylist.text.toString()
                viewModel.onPlaylistCreate(
                    playlist, playlist.pathToPlaylistIcon?.let { Uri.parse(it) })
                val playlistName = "Плейлист ${playlist.playlistName} создан"
                findNavController().navigateUp()
                Toast.makeText(requireContext(), playlistName, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateEditTextState(
        editText: EditText, title: TextView, hasFocus: Boolean, isTextValid: Boolean
    ) {
        if (hasFocus) {
            title.isVisible = true
            editText.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.edit_pressed)
        } else if (isTextValid) {
            title.isVisible = true
            editText.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.edit_pressed)
        } else {
            title.isVisible = false
            editText.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.edit_enabled)
        }
    }
}
