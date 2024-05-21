package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models

import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.UserId

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
