package com.practicum.playlistmaker.sharing

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.sharing.domain.models.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {
    override fun shareLink(shareAppLink: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            setType("text/plain").putExtra(Intent.EXTRA_TEXT, shareAppLink)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(shareIntent)
    }

    override fun openEmail(supportEmailData: EmailData) {
        val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmailData.email))
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, supportEmailData.subject)
        supportIntent.putExtra(Intent.EXTRA_TEXT, supportEmailData.message)
        context.startActivity(supportIntent)
    }

    override fun openLink(termsLink: String) {
        val agreementIntent =
            Intent(Intent.ACTION_VIEW).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        agreementIntent.data = Uri.parse(termsLink)
        context.startActivity(agreementIntent)
    }
}
