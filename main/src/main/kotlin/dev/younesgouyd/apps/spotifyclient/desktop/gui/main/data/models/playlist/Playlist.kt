package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.playlist

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.Image
import kotlinx.serialization.Serializable

/**
 * GET /playlists/{playlist_id}
 */
@Serializable
data class Playlist(
    val description: String?,
    val id: PlaylistId,
    val images: List<Image>?,
    val name: String?
)