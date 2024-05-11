package dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.playlist

import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.UserId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.Image
import kotlinx.serialization.Serializable

/**
 * GET /me/playlists
 */
@Serializable
data class Playlists(
    val next: String?,
    val items: List<SimplifiedPlaylist?>?
) {
    @Serializable
    data class SimplifiedPlaylist(
        val id: PlaylistId,
        val images: List<Image>?,
        val name: String?,
        val owner: Owner?
    ) {
        @Serializable
        data class Owner(
            val id: UserId
        )
    }
}