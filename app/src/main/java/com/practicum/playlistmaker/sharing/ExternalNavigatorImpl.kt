package com.practicum.playlistmaker.sharing

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.sharing.domain.models.EmailData

class ExternalNavigatorImpl(private val context: AppCompatActivity) : ExternalNavigator {
    override fun shareLink(shareAppLink: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, shareAppLink)
        context.startActivity(shareIntent)
    }

    override fun openEmail(supportEmailData: EmailData) {
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmailData.email))
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, supportEmailData.subject)
        supportIntent.putExtra(Intent.EXTRA_TEXT, supportEmailData.message)
        context.startActivity(supportIntent)
    }

    override fun openLink(termsLink: String) {
        val agreementIntent = Intent(Intent.ACTION_VIEW)
        agreementIntent.data = Uri.parse(termsLink)
        context.startActivity(agreementIntent)
    }
}
