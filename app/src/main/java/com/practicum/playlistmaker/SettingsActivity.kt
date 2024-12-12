package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {
    companion object {
        const val TEXT_KEY = "isDarkTheme"
        const val TEXT_VALUE = "ThemePrefs"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbarButton = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarButton)
        val shareButton = findViewById<Button>(R.id.shareButton)
        val supportButton = findViewById<Button>(R.id.supportButton)
        val agreementButton = findViewById<Button>(R.id.agreementButton)
        val switchThemeButton = findViewById<Switch>(R.id.switchThemeButton)

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

        switchThemeButton.isChecked = getSaveThemeState()
        switchThemeButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            saveThemeState(isChecked)
        }
    }

    private fun getSaveThemeState(): Boolean {
        val sharedPreferences = getSharedPreferences(TEXT_VALUE, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(TEXT_KEY, false)
    }

    private fun saveThemeState(isDarkTheme: Boolean) {
        val sharedPreferences = getSharedPreferences(TEXT_VALUE, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(TEXT_KEY, isDarkTheme)
        editor.apply()
    }
}
