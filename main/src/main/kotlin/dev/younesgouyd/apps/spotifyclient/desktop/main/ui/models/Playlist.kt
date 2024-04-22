package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models

import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.UserId

data class Playlist(
    val id: PlaylistId,
    val name: String?,
    val description: String?,
    val images: Images,
    val owner: Owner?,
    val followed: Boolean
) {
    val canUnfollow get() = owner?.id?.value != "spotify"

    data class Track(
        val id: TrackId,
        val name: String?,
        val images: Images
    )

    data class Owner(
        val id: UserId,
        val name: String?
    )
}