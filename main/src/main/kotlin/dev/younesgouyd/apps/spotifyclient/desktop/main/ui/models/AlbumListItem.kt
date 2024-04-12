package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models

import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId

data class AlbumListItem(
    val id: AlbumId,
    val name: String?,
    val images: Images
)