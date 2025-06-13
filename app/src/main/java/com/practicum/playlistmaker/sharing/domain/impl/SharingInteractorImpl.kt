package com.practicum.playlistmaker.sharing.domain.impl

import android.content.Intent
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.models.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {

    override fun shareApp(shareAppLink: String): Intent {
        return externalNavigator.shareLink(shareAppLink)
    }

    override fun openSupport(emailData: EmailData): Intent {
        return externalNavigator.openEmail(emailData)
    }

    override fun openTerms(termsLink: String): Intent {
        return externalNavigator.openLink(termsLink)
    }
}
