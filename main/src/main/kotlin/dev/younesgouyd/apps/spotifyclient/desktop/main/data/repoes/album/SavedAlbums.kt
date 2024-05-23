package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.album

import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.Image
import kotlinx.serialization.Serializable

/**
 * GET /me/albums
 */
@Serializable
data class SavedAlbums(
    val next: String?,
    val items: List<SavedAlbum?>?
) {
    @Serializable
    data class SavedAlbum(
        val album: Album?
    ) {
        @Serializable
        data class Album(
            val id: AlbumId,
            val images: List<Image>?,
            val name: String?,
        )
    }
}
