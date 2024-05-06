package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.user

import dev.younesgouyd.apps.spotifyclient.desktop.main.LazilyLoadedItems
import dev.younesgouyd.apps.spotifyclient.desktop.main.Offset
import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.User

sealed class UserState {
    data object Loading : UserState()

    data class State(
        val user: User,
        val playlists: LazilyLoadedItems<User.Playlist, Offset.Index>,
        val onPlaylistClick: (PlaylistId) -> Unit,
        val onPlayPlaylistClick: (PlaylistId) -> Unit
    ) : UserState()
}