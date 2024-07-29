package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.details

import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Images

data class Artist(
    val id: ArtistId,
    val name: String?,
    val images: Images,
    val genres: List<String>,
    val followerCount: Long?,
    val popularity: Int?,
    val followed: Boolean
) {
    data class Track(
        val id: TrackId,
        val name: String?,
        val images: Images
    )

    data class RelatedArtist(
        val id: ArtistId,
        val name: String?,
        val images: Images,
        val genres: List<String>,
        val followerCount: Long?,
        val popularity: Int?
    )

    data class Album(
        val id: AlbumId,
        val name: String?,
        val images: Images,
        val releaseDate: String?,
        val totalTracks: Int?,
        val artists: List<Artist>
    ) {
        data class Artist(
            val id: ArtistId,
            val name: String?
        )
    }
}
