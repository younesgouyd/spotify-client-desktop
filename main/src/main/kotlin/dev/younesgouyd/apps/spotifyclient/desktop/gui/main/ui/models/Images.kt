package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.ImageUrl

data class Images(
    val large: ImageUrl?, // 640x640
    val medium: ImageUrl?, // 300x300
    val small: ImageUrl?, // 64x64
) {
    companion object {
        fun empty() = Images(null, null, null)
    }

    fun preferablyMedium() = medium ?: small ?: large

    fun preferablySmall() = small ?: medium ?: large
}
