package dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.album

import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import kotlinx.serialization.Serializable

/**
 * GET /albums/{id}/tracks
 */
@Serializable
data class AlbumTracks(
    val items: List<SimplifiedTrackObject>?
) {
    @Serializable
    data class SimplifiedTrackObject(
        val id: TrackId,
        val name: String?
    )
}
