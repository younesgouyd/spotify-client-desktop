package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models

import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.ImageUrl

data class Track(
    val id: TrackId,
    val name: String?,
    val imageUrl: ImageUrl?
)
