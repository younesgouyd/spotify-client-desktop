package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.album

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.Image
import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val id: AlbumId,
    val name: String,
    val images: List<Image>,
    val artists: List<Artist>,
) {
    @Serializable
    data class Artist(
        val id: ArtistId,
        val name: String
    )
}
