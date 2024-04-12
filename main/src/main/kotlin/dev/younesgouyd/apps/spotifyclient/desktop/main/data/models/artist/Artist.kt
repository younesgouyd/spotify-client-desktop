package dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.artist

import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.ImageOfFloatSize
import kotlinx.serialization.Serializable

/**
 * GET /artists/{id}
 */
@Serializable
data class Artist(
    val id: ArtistId,
    val images: List<ImageOfFloatSize>?,
    val name: String?
)
