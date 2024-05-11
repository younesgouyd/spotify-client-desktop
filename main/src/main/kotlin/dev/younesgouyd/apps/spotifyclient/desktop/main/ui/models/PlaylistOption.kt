package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models

import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.ImageUrl

data class PlaylistOption(
    val id: PlaylistId,
    val name: String?,
    val image: ImageUrl?
)