package com.practicum.playlistmaker.sharing.domain.api

interface ExternalNavigator {
    fun shareLink()
    fun openEmail()
    fun openLink()
}
