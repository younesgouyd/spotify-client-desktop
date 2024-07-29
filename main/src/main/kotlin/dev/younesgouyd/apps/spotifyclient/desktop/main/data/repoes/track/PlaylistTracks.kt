package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.track

import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.Image
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * GET /playlists/{id}/tracks
 */
@Serializable
data class PlaylistTracks(
    val next: String?,
    val items: List<PlaylistTrackObject?>?
) {
    @Serializable
    data class PlaylistTrackObject(
        @SerialName("added_at")
        val addedAt: String?,
        val track: TrackObject?
    ) {
        @Serializable
        data class TrackObject(
            val album: Album?,
            val artists: List<Artist>?,
            @SerialName("duration_ms")
            val durationMs: Long?,
            val id: TrackId,
            val name: String?,
            val popularity: Int?
        ) {
            @Serializable
            data class Album(
                val id: AlbumId,
                val images: List<Image>?,
                val name: String?
            )

            @Serializable
            data class Artist(
                val id: ArtistId,
                val name: String?
            )
        }
    }
}
