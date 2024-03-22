package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val url: String?,
    val height: Int?,
    val width: Int?
)