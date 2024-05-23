package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.search

import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Images

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
        val images: Images,
        val artists: List<Artist>
    ) {
        data class Artist(val id: ArtistId, val name: String?)
    }

    data class Playlist(
        val id: PlaylistId,
        val name: String?,
        val images: Images,
        val owner: Owner?
    ) {
        data class Owner(
            val id: UserId,
            val name: String?
        )
    }
}
