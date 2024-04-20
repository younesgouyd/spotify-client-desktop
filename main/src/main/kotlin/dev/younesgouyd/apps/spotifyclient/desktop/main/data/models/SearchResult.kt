package dev.younesgouyd.apps.spotifyclient.desktop.main.data.models

import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import kotlinx.serialization.Serializable

/**
 * GET /search
 */
@Serializable
data class SearchResult(
    val tracks: Tracks? = null,
    val artists: Artists? = null,
    val albums: Albums? = null,
    val playlists: Playlists? = null
) {
    @Serializable
    data class Tracks(
        val items: List<TrackObject?>?
    ) {
        @Serializable
        data class TrackObject(
            val album: Album?,
            val artists: List<Artist?>?,
            val id: TrackId,
            val name: String?
        ) {
            @Serializable
            data class Album(
                val images: List<Image>?
            )

            @Serializable
            data class Artist(
                val id: ArtistId,
                val name: String?
            )
        }
    }

    @Serializable
    data class Artists(
        val items: List<ArtistObject?>?
    ) {
        @Serializable
        data class ArtistObject(
            val id: ArtistId,
            val images: List<ImageOfFloatSize>,
            val name: String?
        )
    }

    @Serializable
    data class Albums(
        val items: List<SimplifiedAlbumObject?>?
    ) {
        @Serializable
        data class SimplifiedAlbumObject(
            val id: AlbumId,
            val images: List<Image>,
            val name: String?
        )
    }

    @Serializable
    data class Playlists(
        val items: List<SimplifiedPlaylistObject?>?
    ) {
        @Serializable
        data class SimplifiedPlaylistObject(
            val id: PlaylistId,
            val images: List<Image>,
            val name: String?
        )
    }
}
