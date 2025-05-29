package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.presentation.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

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

//        val toolbarButton = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarButton)
//        val shareButton = findViewById<Button>(R.id.shareButton)
//        val supportButton = findViewById<Button>(R.id.supportButton)
//        val agreementButton = findViewById<Button>(R.id.agreementButton)
//        val switchThemeButton = findViewById<SwitchCompat>(R.id.switchThemeButton)

        binding.toolbarButton.setNavigationOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        binding.shareButton.setOnClickListener {
            viewModel.shareApp()
//            val message = getString(R.string.messageToShareApp)
//            val shareIntent = Intent(Intent.ACTION_SEND).setType("text/plain")
//                .putExtra(Intent.EXTRA_TEXT, message)
//            startActivity(shareIntent)
        }

        binding.supportButton.setOnClickListener {
            viewModel.openSupport()
//            val email = getString(R.string.email)
//            val subject = getString(R.string.subjectOfMessage)
//            val message = getString(R.string.messageToSupport)
//            val supportIntent = Intent(Intent.ACTION_SENDTO)
//            supportIntent.data = Uri.parse("mailto:")
//            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
//            supportIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
//            supportIntent.putExtra(Intent.EXTRA_TEXT, message)
//            startActivity(supportIntent)
        }

        binding.agreementButton.setOnClickListener {
            viewModel.openTerms()
//            val link = getString(R.string.agreementLink)
//            val agreementIntent = Intent(Intent.ACTION_VIEW)
//            agreementIntent.data = Uri.parse(link)
//            startActivity(agreementIntent)
        }

        binding.switchThemeButton.isChecked = viewModel.getThemeSettings()
//        switchThemeButton.isChecked = (applicationContext as App).getAppTheme()
        binding.switchThemeButton.setOnCheckedChangeListener { _, checked ->
            viewModel.updateThemeSetting(checked)
//            (applicationContext as App).switchTheme(checked)
        }
    }

//    override fun shareLink(shareAppLink: String) {
//        val shareIntent = Intent(Intent.ACTION_SEND).setType("text/plain")
//            .putExtra(Intent.EXTRA_TEXT, shareAppLink)
//        startActivity(shareIntent)
//    }
//
//    override fun openEmail(supportEmailData: EmailData) {
//        val supportIntent = Intent(Intent.ACTION_SENDTO)
//        supportIntent.data = Uri.parse("mailto:")
//        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmailData.email))
//        supportIntent.putExtra(Intent.EXTRA_SUBJECT, supportEmailData.subject)
//        supportIntent.putExtra(Intent.EXTRA_TEXT, supportEmailData.message)
//        startActivity(supportIntent)
//    }
//
//    override fun openLink(termsLink: String) {
//        val agreementIntent = Intent(Intent.ACTION_VIEW)
//        agreementIntent.data = Uri.parse(termsLink)
//        startActivity(agreementIntent)
//    }
}
