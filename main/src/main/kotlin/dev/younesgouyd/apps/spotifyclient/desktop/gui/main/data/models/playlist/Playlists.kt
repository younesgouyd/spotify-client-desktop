package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.playlist

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.Image
import kotlinx.serialization.Serializable

/**
 * GET /me/playlists
 */
@Serializable
data class Playlists(
    val items: List<SimplifiedPlaylist>?
) {
    @Serializable
    data class SimplifiedPlaylist(
        val id: PlaylistId,
        val images: List<Image>?,
        val name: String?
    )
}