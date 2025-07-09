package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.presentation.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = getViewModel()

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            shareButton.setOnClickListener {
                viewModel.shareState()
            }

            supportButton.setOnClickListener {
                viewModel.supportingState()
            }

            agreementButton.setOnClickListener {
                viewModel.agreementState()
            }

            switchThemeButton.setOnCheckedChangeListener { _, checked ->
                viewModel.updateThemeSettings(checked)
            }
        }
    }

    private fun setupObservers() {
        viewModel.getState().observe(viewLifecycleOwner) {
            binding.switchThemeButton.isChecked = it.isDarkTheme
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
