package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.playlist

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.UserId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.Image
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistTracks(
    val href: String,
    val items: List<PlaylistTrackObject>
) {
    @Serializable
    data class PlaylistTrackObject(
        @SerialName("added_at")
        val addedAt: String?,
        @SerialName("added_by")
        val addedBy: AddedBy,
        @SerialName("is_local")
        val isLocal: Boolean,
        val track: TrackObject
    ) {
        @Serializable
        data class AddedBy(
            val id: UserId
        )

        @Serializable
        data class TrackObject(
            val album: Album,
            val artists: List<Artist>,
            val id: TrackId,
            val name: String
        ) {
            @Serializable
            data class Album(
                val id: AlbumId,
                val name: String,
                val images: List<Image>
            )

            @Serializable
            data class Artist(
                val id: ArtistId,
                val name: String
            )
        }
    }
}
