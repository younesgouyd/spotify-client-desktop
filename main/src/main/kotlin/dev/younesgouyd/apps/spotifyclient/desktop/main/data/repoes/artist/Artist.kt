package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.artist

import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.ImageOfFloatSize
import kotlinx.serialization.Serializable

/**
 * GET /artists/{id}
 */
@Serializable
data class Artist(
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
