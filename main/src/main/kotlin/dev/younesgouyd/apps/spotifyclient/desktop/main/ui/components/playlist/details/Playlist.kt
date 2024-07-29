package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.playlist.details

import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Images
import kotlin.time.Duration

data class Playlist(
    val id: PlaylistId,
    val name: String?,
    val description: String?,
    val images: Images,
    val owner: Owner?,
    val followerCount: Long?,
    val followed: Boolean
) {
    val canUnfollow get() = owner?.id?.value != "spotify"

    data class Track(
        val id: TrackId,
        val name: String?,
        val artists: List<Artist>,
        val album: Album?,
        val duration: Duration?,
        val popularity: Int?,
        val addedAt: String?
    ) {
        data class Artist(
            val id: ArtistId,
            val name: String?
        )

        data class Album(
            val id: AlbumId,
            val name: String?,
            val images: Images
        )
    }

    data class Owner(
        val id: UserId,
        val name: String?
    )
}