package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.presentation.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            toolbarButton.setNavigationOnClickListener {
                finish()
                overridePendingTransition(
                    android.R.anim.slide_in_left, android.R.anim.slide_out_right
                )
            }

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
        viewModel.getState().observe(this) {
            binding.switchThemeButton.isChecked = it.isDarkTheme
        }
    }
}
