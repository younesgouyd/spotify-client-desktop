package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.track

import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
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
        val id: TrackId,
        val name: String?
    )
}
