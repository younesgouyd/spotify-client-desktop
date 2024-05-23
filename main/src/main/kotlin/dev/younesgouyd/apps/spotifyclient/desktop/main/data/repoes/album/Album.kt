package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.album

import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.Image
import kotlinx.serialization.Serializable

/**
 * GET /albums/{id}
 */
@Serializable
data class Album(
    val id: AlbumId,
    val name: String?,
    val images: List<Image>?,
    val artists: List<Artist>?
) {
    @Serializable
    data class Artist(
        val id: ArtistId,
        val name: String?
    )
}
