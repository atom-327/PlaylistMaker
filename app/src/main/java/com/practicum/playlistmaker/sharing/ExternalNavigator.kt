package com.practicum.playlistmaker.sharing

import android.content.Intent
import com.practicum.playlistmaker.sharing.domain.models.EmailData

interface ExternalNavigator{
    fun shareLink(shareAppLink: String): Intent
    fun openEmail(supportEmailData: EmailData): Intent
    fun openLink(termsLink: String): Intent
}
