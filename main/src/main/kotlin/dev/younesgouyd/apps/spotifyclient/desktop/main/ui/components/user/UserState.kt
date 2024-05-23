package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.user

import dev.younesgouyd.apps.spotifyclient.desktop.main.LazilyLoadedItems
import dev.younesgouyd.apps.spotifyclient.desktop.main.Offset
import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.UserId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Images

sealed class UserState {
    data object Loading : UserState()

    data class State(
        val user: User,
        val playlists: LazilyLoadedItems<User.Playlist, Offset.Index>,
        val onPlaylistClick: (PlaylistId) -> Unit,
        val onPlayPlaylistClick: (PlaylistId) -> Unit
    ) : UserState()

    data class User(
        val id: UserId,
        val displayName: String?,
        val followerCount: Int?,
        val profilePictureUrl: String?
    ) {
        data class Playlist(
            val id: PlaylistId,
            val name: String?,
            val images: Images
        )
    }
}