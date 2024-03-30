package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.album

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.Image
import kotlinx.serialization.Serializable

/**
 * GET /me/albums
 */
@Serializable
data class SavedAlbums(
    val items: List<SavedAlbum>?
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
