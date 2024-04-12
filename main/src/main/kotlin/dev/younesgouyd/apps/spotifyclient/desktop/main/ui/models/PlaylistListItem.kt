package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models

import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId

data class PlaylistListItem(
    val id: PlaylistId,
    val name: String?,
    val images: Images
)
