package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.user

import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.UserId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Images

data class User(
    val id: UserId,
    val displayName: String?,
    val followerCount: Long?,
    val profilePictureUrl: String?
) {
    data class Playlist(
        val id: PlaylistId,
        val name: String?,
        val images: Images,
        val owner: Owner?
    ) {
        data class Owner(
            val id: UserId,
            val displayName: String?
        )
    }
}