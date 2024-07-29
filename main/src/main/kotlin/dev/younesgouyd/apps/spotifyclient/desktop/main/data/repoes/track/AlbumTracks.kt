package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.track

import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * GET /albums/{id}/tracks
 */
@Serializable
data class AlbumTracks(
    val next: String?,
    val items: List<SimplifiedTrackObject?>?
) {
    @Serializable
    data class SimplifiedTrackObject(
        val artists: List<Artist>?,
        @SerialName("duration_ms")
        val durationMs: Long?,
        val id: TrackId,
        val name: String?
    ) {
        @Serializable
        data class Artist(
            val id: ArtistId,
            val name: String?
        )
    }
}
