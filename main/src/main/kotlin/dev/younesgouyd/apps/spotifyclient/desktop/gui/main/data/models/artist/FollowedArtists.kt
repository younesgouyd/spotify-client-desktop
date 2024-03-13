package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.artist

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.Image
import kotlinx.serialization.Serializable

@Serializable
data class FollowedArtists(
    val artists: Artists
) {
    @Serializable
    data class Artists(
        val items: List<Artist>
    ) {
        @Serializable
        data class Artist(
            val id: ArtistId,
            val images: List<Image>,
            val name: String
        )
    }
}
