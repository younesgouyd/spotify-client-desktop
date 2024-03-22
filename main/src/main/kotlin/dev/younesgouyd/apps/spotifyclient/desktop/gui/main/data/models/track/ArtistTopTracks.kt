package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.track

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.ImageOfFloatSize
import kotlinx.serialization.Serializable

/**
 * GET /artists/{id}/top-tracks
 */
@Serializable
data class ArtistTopTracks(
    val tracks: List<Track>?
) {
    @Serializable
    data class Track(
        val album: Album?,
        val id: TrackId,
        val name: String?
    ) {
        @Serializable
        data class Album(
            val images: List<ImageOfFloatSize>?
        )
    }
}
