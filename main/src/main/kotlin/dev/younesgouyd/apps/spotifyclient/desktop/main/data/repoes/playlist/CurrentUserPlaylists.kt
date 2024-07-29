package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.playlist

import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.UserId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.Image
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * GET /me/playlists
 */
@Serializable
data class CurrentUserPlaylists(
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
            val id: UserId,
            @SerialName("display_name")
            val displayName: String?
        )
    }
}