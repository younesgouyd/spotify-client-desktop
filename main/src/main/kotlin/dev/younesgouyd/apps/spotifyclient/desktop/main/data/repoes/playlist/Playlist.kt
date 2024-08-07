package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.playlist

import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.UserId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.Image
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * GET /playlists/{playlist_id}
 */
@Serializable
data class Playlist(
    val description: String?,
    val followers: Followers?,
    val id: PlaylistId,
    val images: List<Image>?,
    val name: String?,
    val owner: Owner?
) {
    @Serializable
    data class Followers(
        val total: Long?
    )

    @Serializable
    data class Owner(
        val id: UserId,
        @SerialName("display_name")
        val displayName: String?
    )
}