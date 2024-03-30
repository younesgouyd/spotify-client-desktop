package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.AlbumId

data class AlbumListItem(
    val id: AlbumId,
    val name: String?,
    val images: Images
)