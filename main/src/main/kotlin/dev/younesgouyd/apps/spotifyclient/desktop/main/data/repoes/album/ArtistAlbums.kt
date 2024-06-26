package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.album

import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.Image
import kotlinx.serialization.Serializable

/**
 * GET /artists/{id}/albums
 */
@Serializable
data class ArtistAlbums(
    val next: String?,
    val items: List<SimplifiedAlbum?>?
) {
    @Serializable
    data class SimplifiedAlbum(
        val id: AlbumId,
        val images: List<Image>?,
        val name: String?,
        val artists: List<SimplifiedArtist>?
    ) {
        @Serializable
        data class SimplifiedArtist(
            val id: ArtistId,
            val name: String?
        )
    }
}
