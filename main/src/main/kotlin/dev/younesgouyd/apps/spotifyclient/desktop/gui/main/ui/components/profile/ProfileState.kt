package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.profile

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.User

sealed class ProfileState {
    data object Loading : ProfileState()

    data class State(
        val user: User,
        val onLogoutClick: () -> Unit
    ) : ProfileState()
}