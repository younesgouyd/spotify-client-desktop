package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.ImageUrl

data class Images(
    val large: ImageUrl?,
    val medium: ImageUrl?,
    val small: ImageUrl?,
) {
    companion object
}
