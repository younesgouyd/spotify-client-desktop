package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.user

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.User
import kotlinx.coroutines.flow.StateFlow

sealed class UserState {
    data object Loading : UserState()

    data class State(
        val user: User,
        val playlists: StateFlow<List<User.Playlist>>,
        val loadingPlaylists: StateFlow<Boolean>,
        val onLoadPlaylists: () -> Unit,
        val onPlaylistClick: (PlaylistId) -> Unit
    ) : UserState()
}