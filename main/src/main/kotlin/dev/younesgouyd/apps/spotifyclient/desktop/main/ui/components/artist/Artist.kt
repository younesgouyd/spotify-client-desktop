package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist

import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Images

data class Artist(
    val id: ArtistId,
    val name: String?,
    val images: Images,
    val followed: Boolean
) {
    data class Track(
        val id: TrackId,
        val name: String?,
        val images: Images
    )

    data class Album(
        val id: AlbumId,
        val name: String?,
        val images: Images
    )
}
