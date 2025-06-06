package com.practicum.playlistmaker.sharing

import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.sharing.domain.models.EmailData

class ExternalNavigatorImpl : ExternalNavigator {
    override fun shareLink(shareAppLink: String): Intent {
        val shareIntent = Intent(Intent.ACTION_SEND).setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, shareAppLink)
        return shareIntent
    }

    override fun openEmail(supportEmailData: EmailData): Intent {
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmailData.email))
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, supportEmailData.subject)
        supportIntent.putExtra(Intent.EXTRA_TEXT, supportEmailData.message)
        return supportIntent
    }

    override fun openLink(termsLink: String): Intent {
        val agreementIntent = Intent(Intent.ACTION_VIEW)
        agreementIntent.data = Uri.parse(termsLink)
        return agreementIntent
    }
}
