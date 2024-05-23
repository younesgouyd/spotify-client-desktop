package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.playlist.list

import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Images

data class PlaylistListItem(
    val id: PlaylistId,
    val name: String?,
    val images: Images
)
