package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.core.creator.Creator
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.presentation.view_model.SettingsViewModel
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsActivity : AppCompatActivity() {

    companion object {
        private const val STATE_SHARING = 1
        private const val STATE_SUPPORTING = 2
        private const val STATE_AGREEMENT = 3
    }

    private lateinit var settingsInteractor: SettingsInteractor
    private lateinit var sharingInteractor: SharingInteractor

    private lateinit var binding: ActivitySettingsBinding

    private val viewModel by lazy {
        ViewModelProvider(
            this, SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingsInteractor = Creator.provideSettingsInteractor(this.applicationContext)
        sharingInteractor = Creator.provideSharingInteractor(this)

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
                viewModel.renderState(STATE_SHARING)
            }

            supportButton.setOnClickListener {
                viewModel.renderState(STATE_SUPPORTING)
            }

            agreementButton.setOnClickListener {
                viewModel.renderState(STATE_AGREEMENT)
            }

            switchThemeButton.isChecked = settingsInteractor.getThemeSettings()
            switchThemeButton.setOnCheckedChangeListener { _, checked ->
                settingsInteractor.updateThemeSetting(checked)
            }
        }
    }

    private fun setupObservers() {
        viewModel.getState().observe(this) { state ->
            render(state)
        }
    }

    private fun render(state: Int) {
        when (state) {
            STATE_SHARING -> toShareIntent()
            STATE_SUPPORTING -> toSupportIntent()
            STATE_AGREEMENT -> toAgreementIntent()
        }
    }

    private fun toShareIntent() {
        sharingInteractor.shareApp()
    }

    private fun toSupportIntent() {
        sharingInteractor.openSupport()
    }

    private fun toAgreementIntent() {
        sharingInteractor.openTerms()
    }
}
