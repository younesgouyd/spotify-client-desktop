package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.TrackId

data class Playlist(
    val id: PlaylistId,
    val name: String,
    val description: String,
    val images: Images
) {
    data class Track(
        val id: TrackId,
        val name: String,
        val artists: List<Artist>,
        val album: Album,
        val images: Images,
        val playing: Boolean
    ) {
        data class Artist(
            val id: ArtistId,
            val name: String
        )

        data class Album(
            val id: AlbumId,
            val name: String
        )
    }
}