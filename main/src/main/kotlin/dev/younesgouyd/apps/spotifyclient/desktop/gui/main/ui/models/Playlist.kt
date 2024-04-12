package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.UserId

data class Playlist(
    val id: PlaylistId,
    val name: String?,
    val description: String?,
    val images: Images,
    val owner: Owner?
) {
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