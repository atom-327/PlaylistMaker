package com.practicum.playlistmaker.sharing.domain.impl

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.models.EmailData

class SharingInteractorImpl(
    private val context: Context,
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    private fun getShareAppLink(): String {
        return context.getString(R.string.messageToShareApp)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            context.getString(R.string.email),
            context.getString(R.string.subjectOfMessage),
            context.getString(R.string.messageToSupport)
        )
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.agreementLink)
    }
}
