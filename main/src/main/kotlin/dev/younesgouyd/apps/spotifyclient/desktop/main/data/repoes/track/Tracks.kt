package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.track

import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.Image
import kotlinx.serialization.Serializable

/**
 * Get Several Tracks
 * GET /tracks
 */
@Serializable
data class Tracks(
    val tracks: List<Track?>?
) {
    @Serializable
    data class Track(
        val album: Album?,
        val id: TrackId,
        val name: String?
    ) {
        @Serializable
        data class Album(
            val images: List<Image>?
        )
    }
}