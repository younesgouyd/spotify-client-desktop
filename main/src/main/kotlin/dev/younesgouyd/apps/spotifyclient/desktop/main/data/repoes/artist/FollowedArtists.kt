package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.artist

import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.Image
import kotlinx.serialization.Serializable

/**
 * GET /me/following
 */
@Serializable
data class FollowedArtists(
    val artists: Artists?
) {
    @Serializable
    data class Artists(
        val next: String?,
        val items: List<Artist>?
    ) {
        @Serializable
        data class Artist(
            val id: ArtistId,
            val images: List<Image>?,
            val name: String?
        )
    }
}
