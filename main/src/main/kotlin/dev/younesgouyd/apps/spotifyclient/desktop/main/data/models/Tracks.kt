package dev.younesgouyd.apps.spotifyclient.desktop.main.data.models

import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import kotlinx.serialization.Serializable

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