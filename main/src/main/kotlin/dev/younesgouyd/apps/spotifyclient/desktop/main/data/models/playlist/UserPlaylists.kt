package dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.playlist

import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.Image
import kotlinx.serialization.Serializable

/**
 * GET /users/{user_id}/playlists
 */
@Serializable
data class UserPlaylists(
    val items: List<SimplifiedPlaylistObject?>?
) {
    @Serializable
    data class SimplifiedPlaylistObject(
        val description: String?,
        val id: PlaylistId,
        val images: List<Image>?,
        val name: String?
    )
}