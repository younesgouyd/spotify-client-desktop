package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.search

import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.Image
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.ImageOfFloatSize
import kotlinx.serialization.SerialName
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
            val genres: List<String>?, // todo
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
            @SerialName("total_tracks")
            val totalTracks: Int?, // todo
            val id: AlbumId,
            val images: List<Image>,
            val name: String?,
            @SerialName("release_date")
            val releaseDate: String?, // todo
            val artists: List<SimplifiedArtistObject?>?
        ) {
            @Serializable
            data class SimplifiedArtistObject(
                val id: ArtistId,
                val name: String?
            )
        }
    }

    @Serializable
    data class Playlists(
        val items: List<SimplifiedPlaylistObject?>?
    ) {
        @Serializable
        data class SimplifiedPlaylistObject(
            val id: PlaylistId,
            val images: List<Image>,
            val name: String?,
            val owner: Owner?
        ) {
            @Serializable
            data class Owner(
                val id: UserId,
                @SerialName("display_name")
                val displayName: String?
            )
        }
    }
}
