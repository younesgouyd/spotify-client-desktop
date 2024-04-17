package dev.younesgouyd.apps.spotifyclient.desktop.main.data.models

import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import kotlinx.serialization.Serializable

@Serializable
data class CategoryPlaylists(
    val playlists: Playlists?
) {
    @Serializable
    data class Playlists(
        val items: List<SimplifiedPlaylistObject?>?
    ) {
        @Serializable
        data class SimplifiedPlaylistObject(
            val id: PlaylistId,
            val images: List<Image>?,
            val name: String?
        )
    }
}
