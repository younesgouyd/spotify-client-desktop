package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models

import dev.younesgouyd.apps.spotifyclient.desktop.main.FolderId
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId

data class Folder(
    val id: FolderId,
    val name: String
) {
    data class Track(
        val id: TrackId,
        val name: String?,
        val images: Images
    )
}