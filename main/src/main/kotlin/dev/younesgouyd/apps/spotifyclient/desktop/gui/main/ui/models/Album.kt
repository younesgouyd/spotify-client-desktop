package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.TrackId

data class Album(
    val id: AlbumId,
    val name: String,
    val artist: List<Artist>,
    val images: Images
) {
    data class Artist(
        val id: ArtistId,
        val name: String
    )

    data class Track(
        val id: TrackId,
        val name: String
    )
}
