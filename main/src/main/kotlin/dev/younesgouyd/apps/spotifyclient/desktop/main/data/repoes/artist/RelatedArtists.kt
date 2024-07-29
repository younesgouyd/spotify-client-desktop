package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.artist

import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.ImageOfFloatSize
import kotlinx.serialization.Serializable

@Serializable
data class RelatedArtists(
    val artists: List<ArtistObject?>?
) {
    @Serializable
    data class ArtistObject(
        val followers: Followers?,
        val genres: List<String>?,
        val id: ArtistId,
        val images: List<ImageOfFloatSize>?,
        val name: String?,
        val popularity: Int?
    ) {
        @Serializable
        data class Followers(
            val total: Long?
        )
    }
}