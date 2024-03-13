package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.PlaylistId

data class SimplifiedPlaylist(
    val id: PlaylistId,
    val name: String,
    val images: Images
)
