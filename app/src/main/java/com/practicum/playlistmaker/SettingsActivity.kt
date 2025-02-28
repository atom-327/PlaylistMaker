package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : AppCompatActivity() {
    companion object {
        const val SHARED_PREF = "ThemePrefs"
        const val TEXT_KEY = "isDarkTheme"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbarButton = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarButton)
        val shareButton = findViewById<Button>(R.id.shareButton)
        val supportButton = findViewById<Button>(R.id.supportButton)
        val agreementButton = findViewById<Button>(R.id.agreementButton)
        val switchThemeButton = findViewById<SwitchCompat>(R.id.switchThemeButton)

        toolbarButton.setNavigationOnClickListener {
            val returnIntent = Intent(this, MainActivity::class.java)
            startActivity(returnIntent)
            finish()
        }

        shareButton.setOnClickListener {
            val message = getString(R.string.messageToShareApp)
            val shareIntent = Intent(Intent.ACTION_SEND).setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, message)
            startActivity(shareIntent)
        }

        supportButton.setOnClickListener {
            val subject = getString(R.string.subjectOfMessage)
            val message = getString(R.string.messageToSupport)
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("machoman1488@yandex.ru"))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            supportIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(supportIntent)
        }

        agreementButton.setOnClickListener {
            val link = getString(R.string.agreementLink)
            val agreementIntent = Intent(Intent.ACTION_VIEW)
            agreementIntent.data = Uri.parse(link)
            startActivity(agreementIntent)
        }

        val sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE)
        switchThemeButton.isChecked = sharedPreferences.getBoolean(TEXT_KEY, false)
        switchThemeButton.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
            sharedPreferences.edit().putBoolean(TEXT_KEY, checked).apply()
        }
    }
}
