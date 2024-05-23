package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.album.list

import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Images

data class AlbumListItem(
    val id: AlbumId,
    val name: String?,
    val images: Images
)