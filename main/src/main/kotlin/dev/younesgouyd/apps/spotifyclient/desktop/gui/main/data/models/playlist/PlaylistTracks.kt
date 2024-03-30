package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.playlist

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.Image
import kotlinx.serialization.Serializable

/**
 * GET /playlists/{id}/tracks
 */
@Serializable
data class PlaylistTracks(
    val items: List<PlaylistTrackObject>?
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
