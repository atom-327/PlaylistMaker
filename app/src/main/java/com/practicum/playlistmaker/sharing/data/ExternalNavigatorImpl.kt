package com.practicum.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.models.EmailData
import androidx.core.net.toUri

class ExternalNavigatorImpl(private val app: Context) : ExternalNavigator {
    override fun shareLink() {
        val shareAppLink = getShareAppLink()
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareAppLink)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        app.startActivity(shareIntent)
    }

    override fun openEmail() {
        val supportEmailData = getSupportEmailData()
        val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        supportIntent.data = "mailto:".toUri()
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmailData.email))
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, supportEmailData.subject)
        supportIntent.putExtra(Intent.EXTRA_TEXT, supportEmailData.message)
        app.startActivity(supportIntent)
    }

    override fun openLink() {
        val termsLink = getTermsLink()
        val agreementIntent = Intent(Intent.ACTION_VIEW).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        agreementIntent.data = termsLink.toUri()
        app.startActivity(agreementIntent)
    }

    private fun getShareAppLink(): String {
        return app.getString(R.string.messageToShareApp)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            app.getString(R.string.email),
            app.getString(R.string.subjectOfMessage),
            app.getString(R.string.messageToSupport)
        )
    }

    private fun getTermsLink(): String {
        return app.getString(R.string.agreementLink)
    }
}
