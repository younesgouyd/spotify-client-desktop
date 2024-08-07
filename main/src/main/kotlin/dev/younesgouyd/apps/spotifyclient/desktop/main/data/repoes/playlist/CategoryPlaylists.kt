package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.playlist

import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.UserId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.Image
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * GET /browse/categories/{category_id}/playlists
 */
@Serializable
data class CategoryPlaylists(
    val playlists: Playlists?
) {
    @Serializable
    data class Playlists(
        val next: String?,
        val items: List<SimplifiedPlaylistObject?>?
    ) {
        @Serializable
        data class SimplifiedPlaylistObject(
            val id: PlaylistId,
            val images: List<Image>?,
            val name: String?,
            val owner: Owner?
        ) {
            @Serializable
            data class Owner(
                val id: UserId,
                @SerialName("display_name")
                val displayName: String?
            )
        }
    }
}
