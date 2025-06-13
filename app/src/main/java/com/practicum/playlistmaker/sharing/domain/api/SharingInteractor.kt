package com.practicum.playlistmaker.sharing.domain.api

import android.content.Intent
import com.practicum.playlistmaker.sharing.domain.models.EmailData

interface SharingInteractor {
    fun shareApp(shareAppLink: String): Intent
    fun openSupport(emailData: EmailData): Intent
    fun openTerms(termsLink: String): Intent
}
