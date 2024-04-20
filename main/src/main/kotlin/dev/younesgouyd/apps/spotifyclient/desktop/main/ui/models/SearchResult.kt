package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models

import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId

data class SearchResult(
    val tracks: List<Track>,
    val artists: List<Artist>,
    val albums: List<Album>,
    val playlists: List<Playlist>
) {
    data class Track(
        val id: TrackId,
        val name: String?,
        val images: Images,
        val artists: List<Artist>
    ) {
        data class Artist(
            val id: ArtistId,
            val name: String?
        )
    }

    data class Artist(
        val id: ArtistId,
        val name: String?,
        val images: Images
    )

    data class Album(
        val id: AlbumId,
        val name: String?,
        val images: Images
    )

    data class Playlist(
        val id: PlaylistId,
        val name: String?,
        val images: Images
    )
}
