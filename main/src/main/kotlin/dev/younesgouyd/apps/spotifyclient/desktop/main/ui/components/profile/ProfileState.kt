package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.profile

sealed class ProfileState {
    data object Loading : ProfileState()

    data class State(
        val user: User,
        val onLogoutClick: () -> Unit
    ) : ProfileState()
}