package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.track

import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.Image
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
        val track: TrackObject?
    ) {
        @Serializable
        data class TrackObject(
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
}
