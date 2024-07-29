package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.user

import dev.younesgouyd.apps.spotifyclient.desktop.main.LazilyLoadedItems
import dev.younesgouyd.apps.spotifyclient.desktop.main.Offset
import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.UserId

sealed class UserDetailsState {
    data object Loading : UserDetailsState()

    data class State(
        val user: User,
        val playlists: LazilyLoadedItems<User.Playlist, Offset.Index>,
        val onPlaylistClick: (PlaylistId) -> Unit,
        val onOwnerClick: (UserId) -> Unit,
        val onPlayPlaylistClick: (PlaylistId) -> Unit
    ) : UserDetailsState()


}