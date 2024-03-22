package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.playlist

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.Image
import kotlinx.serialization.Serializable

@Serializable
data class Playlists(
    val href: String,
    val limit: Int,
    val next: String?,
    val offset: Int,
    val previous: String?,
    val total: Int,
    val items: List<SimplifiedPlaylist>
) {
    @Serializable
    data class SimplifiedPlaylist(
        val id: PlaylistId,
        val images: List<Image>?,
        val name: String
    )
}