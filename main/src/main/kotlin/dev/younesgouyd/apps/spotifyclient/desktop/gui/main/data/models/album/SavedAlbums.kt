package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.album

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.Image
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SavedAlbums(
    val items: List<SavedAlbum>
) {
    @Serializable
    data class SavedAlbum(
        @SerialName("added_at")
        val addedAt: String,
        val album: Album
    ) {
        @Serializable
        data class Album(
            val id: AlbumId,
            val images: List<Image>,
            val name: String,
            val artists: List<Artist>
        ) {
            @Serializable
            data class Artist(
                val id: ArtistId,
                val name: String
            )
        }
    }
}
