package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.album

import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.Image
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * GET /me/albums
 */
@Serializable
data class SavedAlbums(
    val next: String?,
    val items: List<SavedAlbum?>?
) {
    @Serializable
    data class SavedAlbum(
        val album: Album?
    ) {
        @Serializable
        data class Album(
            @SerialName("total_tracks")
            val totalTracks: Int?,
            val id: AlbumId,
            val images: List<Image>?,
            val name: String?,
            @SerialName("release_date")
            val releaseDate: String?,
            val artists: List<Artist>?,
            val genres: List<String>?,
            val popularity: Int?
        ) {
            @Serializable
            data class Artist(
                val id: ArtistId,
                val name: String?
            )
        }
    }
}
