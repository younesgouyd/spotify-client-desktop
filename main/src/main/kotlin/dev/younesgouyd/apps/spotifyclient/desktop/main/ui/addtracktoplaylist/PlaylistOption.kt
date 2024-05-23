package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist

import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.ImageUrl

data class PlaylistOption(
    val id: PlaylistId,
    val name: String?,
    val image: ImageUrl?
)