package com.practicum.playlistmaker.sharing

import com.practicum.playlistmaker.sharing.domain.models.EmailData

interface ExternalNavigator{
    fun shareLink(shareAppLink: String)
    fun openEmail(supportEmailData: EmailData)
    fun openLink(termsLink: String)
}
