package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.UserId

data class User(
    val id: UserId,
    val displayName: String?,
    val followerCount: Int?,
    val profilePictureUrl: String?
) {
    data class Playlist(
        val id: PlaylistId,
        val name: String?,
        val description: String?,
        val images: Images
    )
}
