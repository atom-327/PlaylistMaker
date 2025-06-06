package com.practicum.playlistmaker.sharing.domain.impl

import android.content.Intent
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.models.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {

    override fun shareApp(): Intent {
        return externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms(): Intent {
        return externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport(): Intent {
        return externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return R.string.messageToShareApp.toString()
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            R.string.email.toString(),
            R.string.subjectOfMessage.toString(),
            R.string.messageToSupport.toString()
        )
    }

    private fun getTermsLink(): String {
        return R.string.agreementLink.toString()
    }
}
