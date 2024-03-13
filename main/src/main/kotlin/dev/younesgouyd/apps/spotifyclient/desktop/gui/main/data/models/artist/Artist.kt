package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.artist

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.ImageOfFloatSize
import kotlinx.serialization.Serializable

@Serializable
data class Artist(
    val id: ArtistId,
    val images: List<ImageOfFloatSize>,
    val name: String
)
