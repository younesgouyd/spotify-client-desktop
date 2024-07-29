package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.list

import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Images

data class ArtistItem(
    val id: ArtistId,
    val name: String?,
    val images: Images
)